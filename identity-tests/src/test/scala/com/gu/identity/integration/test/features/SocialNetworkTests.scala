package com.gu.identity.integration.test.features

import com.gu.identity.integration.test.IdentitySeleniumTestSuite
import com.gu.identity.integration.test.pages.{ContainerWithSigninModulePage, FrontPage}
import com.gu.identity.integration.test.steps.{SignInSteps, SocialNetworkSteps, UserSteps}
import com.gu.identity.integration.test.tags.{CoreTest, OptionalTest, SocialTest}
import com.gu.identity.integration.test.util.facebook.FacebookTestUser
import com.gu.integration.test.steps.BaseSteps
import org.openqa.selenium.WebDriver
import org.scalatest.Tag


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

    scenarioFacebook("SN3: should be auto signed in if registered and logged into Facebook", OptionalTest, SocialTest) {
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
}
