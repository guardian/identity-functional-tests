package com.gu.identity.integration.test.features

import java.net.URLEncoder
import java.util.UUID

import com.gu.automation.support.{Browser, Config}
import com.gu.identity.integration.test.IdentitySeleniumTestSuite
import com.gu.identity.integration.test.pages.{ContainerWithSigninModulePage, FrontPage}
import com.gu.identity.integration.test.steps.{SignInSteps, SocialNetworkSteps, UserSteps}
import com.gu.identity.integration.test.tags.{CoreTest, OptionalTest, SocialTest, Unstable}
import com.gu.identity.integration.test.util.facebook.FacebookTestUser
import com.gu.integration.test.steps.BaseSteps
import org.joda.time.DateTime
import org.openqa.selenium.{OutputType, TakesScreenshot, WebDriver}
import org.scalatest.Tag
import org.slf4j.MDC


class SocialNetworkTests extends IdentitySeleniumTestSuite {
  //Extends scenarioWeb to include setup/tear down of the test facebook user as there is a hard limit on users allowed
  //BeforeAndAfter was not used as does not work with scenarioWeb
  protected def scenarioFacebook(specText: String, testTags: Tag*)(testFunction: WebDriver => FacebookTestUser => Any) {
    scenarioWeb(specText, testTags: _*)({ implicit driver: WebDriver =>
      val facebookUser = SocialNetworkSteps().createNewFacebookTestUser()
      try testFunction(driver)(facebookUser)
      finally SocialNetworkSteps().deleteFacebookTestUser(facebookUser)
    })
  }

  //Extends scenarioWeb to not run Chrome browser for Google tests due to browser related bug
  protected def scenarioGoogle(specText: String, testTags: Tag*)(testFunction: WebDriver => Any) {
    def executeTestWithScreenshotOnException[T <: WebDriver](testFunction: T => Any, driver: T, testName: String) = {
      try {
        testFunction(driver)
      } catch {
        case e: Exception => failWithScreenshot(testName, driver, e)
      } finally {
        driver.quit()
      }
    }
    def failWithScreenshot(testName: String, driver: WebDriver, e: Exception) = {
      logger.error("Test failed")
      try {
        driver match {
          case ts: TakesScreenshot => {
            logger.info(s"[FAILED]${e.getMessage}")
            logger.info("[SCREENSHOT]", ts.getScreenshotAs(OutputType.FILE))
          }
          case _ => throw new RuntimeException("Driver can't take screen shots.")
        }
      } catch {
        case e: Exception => logger.error("Error taking screen shot.")}
      throw e
    }
    val list: List[Browser] = Config().getBrowsers()
    val browsers: List[Browser] = list diff List[Browser] {Browser("chrome", None)}
    browsers.foreach(browser => {
      val browserEnhancedSpecText = s"$specText on $browser"
      scenario(browserEnhancedSpecText, testTags: _*)({ td =>
        val tstashBaseURL = Config().getPluginValue("teststash.url")
        sys.props.put("teststash.url", tstashBaseURL)
        val setName = Config().getProjectName()
        val setDate = DateTime.now.getMillis.toString
        MDC.put("ID", UUID.randomUUID().toString)
        MDC.put("setName", setName)
        MDC.put("setDate", setDate)
        MDC.put("testName", td.name)
        MDC.put("testDate", DateTime.now.getMillis.toString)
        MDC.put("phase", "STEP")
        val tstashName = URLEncoder.encode(setName, "UTF-8")
        val testRunId = Config().getTestRunId().getOrElse("")
        val tstashURL = s"$tstashBaseURL/setLookup?setName=$tstashName&setDate=$setDate"
        logger.info(s"[StartInfo]$tstashURL $testRunId")
        logger.info("Test Name: " + td.name)

        val driver = startDriver(td.name, browser)
        executeTestWithScreenshotOnException(testFunction, driver, td.name)
      })
    })
  }

  feature("Registration and sign-in using Facebook") {
    scenarioFacebook("SN1: should be able to register using Facebook", OptionalTest, SocialTest) {
      implicit driver: WebDriver => implicit facebookUser: FacebookTestUser =>
        SocialNetworkSteps().goToFacebookAsUser(facebookUser)
        BaseSteps().goToStartPage()
        val registerPage = SignInSteps().clickSignInLink().clickRegisterNewUserLink()
        val authDialog = registerPage.clickRegisterWithFacebookButton()
        authDialog.clickConfirmButton(facebookUser.email.get, facebookUser.password.get)
        SignInSteps().checkUserIsLoggedIn(facebookUser.fullName)
        SignInSteps().checkThatLoginCookieExists()
    }

    scenarioFacebook("SN2: should get an error message if Facebook e-mail permissions are missing", OptionalTest, SocialTest) {
      implicit driver: WebDriver => implicit facebookUser: FacebookTestUser =>
        val email: String = facebookUser.email.get
        val pwd: String = facebookUser.password.get
        SocialNetworkSteps().goToFacebookAsUser(facebookUser)
        BaseSteps().goToStartPage()
        val registerPage = SignInSteps().clickSignInLink().clickRegisterNewUserLink()
        val authDialog = registerPage.clickRegisterWithFacebookButton()
        authDialog.clickEditInformationProvided(email, pwd).clickEmailCheckBox().clickConfirmButton(email, pwd)
        SocialNetworkSteps().checkUserGotFacebookEmailError(registerPage)
        SignInSteps().checkUserIsNotLoggedIn(facebookUser.fullName)
    }

    scenarioFacebook("SN3: should be auto signed in if registered and logged into Facebook", CoreTest, SocialTest) {
      implicit driver: WebDriver => implicit facebookUser: FacebookTestUser =>
        SocialNetworkSteps().goToFacebookAsUser(facebookUser)
        val frontPage = new FrontPage()
        BaseSteps().goToStartPage()
        val registerPage = SignInSteps().clickSignInLink().clickRegisterNewUserLink()
        val authDialog = registerPage.clickRegisterWithFacebookButton()
        authDialog.clickConfirmButton(facebookUser.email.get, facebookUser.password.get)
        SignInSteps().signOut(frontPage)
        val newLogOutTime = System.currentTimeMillis / 1000 - 24 * 3600
        SignInSteps().setSignOutCookieWithTime(newLogOutTime)
        SocialNetworkSteps().clearLocalStorageFacebookValue()
        BaseSteps().goToStartPage()
        SocialNetworkSteps().checkUserGotAutoSignInBanner(frontPage)
        SignInSteps().checkUserIsLoggedIn(facebookUser.fullName)
    }

    scenarioFacebook("SN4: should be asked to re-authenticate when editing profile after logging in with Facebook", OptionalTest, SocialTest) {
      implicit driver: WebDriver => implicit facebookUser: FacebookTestUser =>
        SocialNetworkSteps().goToFacebookAsUser(facebookUser)
        BaseSteps().goToStartPage()
        val registerPage = SignInSteps().clickSignInLink().clickRegisterNewUserLink()
        val authDialog = registerPage.clickRegisterWithFacebookButton()
        authDialog.clickConfirmButton(facebookUser.email.get, facebookUser.password.get)
        val editAccountDetailsPage = UserSteps().goToEditProfilePage(new ContainerWithSigninModulePage())
        SocialNetworkSteps().checkUserGotReAuthenticationMessage(editAccountDetailsPage)
        val facebookConfirmPasswordDialog = editAccountDetailsPage.clickConfirmWithFacebookButton
        val editProfilePage = facebookConfirmPasswordDialog.enterPassword(facebookUser.password.get).clickContinueButton()
        SocialNetworkSteps().checkUserIsOnEditProfilePage(editProfilePage)
    }

    scenarioFacebook("SN5: should stay on Facebook when entering wrong Facebook password during re-authentication", OptionalTest, SocialTest) {
      implicit driver: WebDriver => implicit facebookUser: FacebookTestUser =>
        SocialNetworkSteps().goToFacebookAsUser(facebookUser)
        BaseSteps().goToStartPage()
        val registerPage = SignInSteps().clickSignInLink().clickRegisterNewUserLink()
        val authDialog = registerPage.clickRegisterWithFacebookButton()
        authDialog.clickConfirmButton(facebookUser.email.get, facebookUser.password.get)
        val editAccountDetailsPage = UserSteps().goToEditProfilePage(new ContainerWithSigninModulePage())
        SocialNetworkSteps().checkUserGotReAuthenticationMessage(editAccountDetailsPage)
        val facebookConfirmPasswordDialog = editAccountDetailsPage.clickConfirmWithFacebookButton
        facebookConfirmPasswordDialog.enterPassword("wrongpassword").clickContinueButton()
        SocialNetworkSteps().checkUserIsOnFacebook()
    }

    scenarioFacebook("SN6: Facebook user can change email on profile and still sign in", OptionalTest, SocialTest, Unstable) {
      implicit driver: WebDriver => implicit facebookUser: FacebookTestUser =>
        val newEmail = System.currentTimeMillis() + "changed@changed.com"
        val oldEmail: String = facebookUser.email.get
        val pwd: String = facebookUser.password.get
        //setup
        SocialNetworkSteps().goToFacebookAsUser(facebookUser)
        BaseSteps().goToStartPage()
        val registerPage = SignInSteps().clickSignInLink().clickRegisterNewUserLink()
        registerPage.clickRegisterWithFacebookButton().clickConfirmButton(oldEmail, pwd)
        val editAccountDetailsPage = UserSteps().goToEditAccountPage(
          new ContainerWithSigninModulePage()).clickConfirmWithFacebookButton
          .enterPassword(pwd).clickContinueButton().clickEditAccountDetailsTab()
        //change email
        editAccountDetailsPage.enterEmailAddress(newEmail)
        editAccountDetailsPage.saveChanges()
        SignInSteps().signOut(new ContainerWithSigninModulePage())
        SignInSteps().clickSignInLink()
        SignInSteps().clickSignInWithFacebook().left.get
        SignInSteps().checkUserIsLoggedIn(facebookUser.fullName)
        BaseSteps().goToStartPage()
        val signInEmail = UserSteps().goToEditAccountPage(new ContainerWithSigninModulePage())
          .clickEditAccountDetailsTab().getEmailAddress

        signInEmail should be(newEmail) //confirms facebook sign in was against correctly changed email
    }


    scenarioFacebook("SN7: should be asked to re-request Facebook e-mail permissions after denying them the first time",
      OptionalTest, SocialTest) {
      implicit driver: WebDriver => implicit facebookUser: FacebookTestUser =>
        val email: String = facebookUser.email.get
        val pwd: String = facebookUser.password.get
        SocialNetworkSteps().goToFacebookAsUser(facebookUser)
        BaseSteps().goToStartPage()
        val registerPage = SignInSteps().clickSignInLink().clickRegisterNewUserLink()
        val authDialog = registerPage.clickRegisterWithFacebookButton()
        authDialog.clickEditInformationProvided(email, pwd).clickEmailCheckBox().clickConfirmButton(email, pwd)
        SocialNetworkSteps().checkUserGotFacebookEmailError(registerPage)
        BaseSteps().goToStartPage()
        SignInSteps().checkUserIsNotLoggedIn(facebookUser.fullName)
        SignInSteps().clickSignInLink()
        val registerPage2 = SignInSteps().clickSignInWithFacebook(false).right.get
        val authDialog2 = registerPage2.clickRegisterWithFacebookButton()
        authDialog2.clickConfirmButton(email, pwd)
        SignInSteps().checkUserIsLoggedIn(facebookUser.fullName)
    }

  }
  feature("Registration and sign-in using Google") {

    scenarioGoogle("SN8: should be asked to re-authenticate when editing profile after logging in with Google", OptionalTest, SocialTest, Unstable) {
      implicit driver: WebDriver =>
        SignInSteps().signInUsingGoogle()
        val editAccountDetailsPage = UserSteps().goToEditProfilePage(new ContainerWithSigninModulePage())
        SocialNetworkSteps().checkUserGotReAuthenticationMessage(editAccountDetailsPage)
        val googleConfirmPasswordDialog = editAccountDetailsPage.clickConfirmWithGoogleButton
        val editProfilePage = googleConfirmPasswordDialog.clickChooseAccountButton()
        SocialNetworkSteps().checkUserIsOnEditProfilePage(editProfilePage)
    }
  }
}
