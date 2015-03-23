package com.gu.identity.integration.test.features

import com.gu.automation.support.Config
import com.gu.identity.integration.test.IdentitySeleniumTestSuite
import com.gu.identity.integration.test.pages.{FrontPage, FaceBookAuthDialog}
import com.gu.identity.integration.test.steps.{UserSteps, SignInSteps}
import com.gu.integration.test.steps.{SocialNetworkSteps, BaseSteps}
import com.gu.integration.test.util.UserConfig._
import com.gu.identity.integration.test.util.facebook._
import org.openqa.selenium.WebDriver
import com.gu.integration.test.util.CookieUtil._


class SocialNetworkTests extends IdentitySeleniumTestSuite {

  feature("Registration and sign-in using Facebook") {
    scenarioWeb("should be able to register using Facebook") { implicit driver: WebDriver =>
      val facebookUser = SocialNetworkSteps().createNewFacebookTestUser()
      SocialNetworkSteps().goToFacebookAsUser(facebookUser)
      BaseSteps().goToStartPage()
      val registerPage = SignInSteps().clickSignInLink().clickRegisterNewUserLink()
      val authDialog = registerPage.clickRegisterWithFacebookButton()
      authDialog.clickConfirmButton()
      SignInSteps().checkUserIsLoggedIn(facebookUser.fullName)
      SignInSteps().checkThatLoginCookieExists()
      SocialNetworkSteps().deleteFacebookTestUser(facebookUser)
    }

    scenarioWeb("should get an error message if e-mail permissions are missing") { implicit driver: WebDriver =>
      val facebookUser = SocialNetworkSteps().createNewFacebookTestUser()
      SocialNetworkSteps().goToFacebookAsUser(facebookUser)
      BaseSteps().goToStartPage()
      val registerPage = SignInSteps().clickSignInLink().clickRegisterNewUserLink()
      val authDialog = registerPage.clickRegisterWithFacebookButton()
      authDialog.clickEditInformationProvided().clickEmailCheckBox().clickConfirmButton()
      SocialNetworkSteps().checkUserGotFacebookEmailError(registerPage)
      SignInSteps().checkUserIsNotLoggedIn(facebookUser.fullName)
      SocialNetworkSteps().deleteFacebookTestUser(facebookUser)
    }

    scenarioWeb("should be auto signed in if registered and logged into Facebook") { implicit driver: WebDriver =>
      val facebookUser = SocialNetworkSteps().createNewFacebookTestUser()
      SocialNetworkSteps().goToFacebookAsUser(facebookUser)
      val frontPage = new FrontPage()
      BaseSteps().goToStartPage()
      val registerPage = SignInSteps().clickSignInLink().clickRegisterNewUserLink()
      val authDialog = registerPage.clickRegisterWithFacebookButton()
      authDialog.clickConfirmButton()
      SignInSteps().clearLoginCookies()
      BaseSteps().goToStartPage()
      SocialNetworkSteps().checkUserGotAutoSignInBanner(frontPage)
      SignInSteps().checkUserIsLoggedIn(facebookUser.fullName)
      SocialNetworkSteps().deleteFacebookTestUser(facebookUser)
    }

  }
}
