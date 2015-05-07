package com.gu.identity.integration.test.features

import com.gu.identity.integration.test.IdentitySeleniumTestSuite
import com.gu.identity.integration.test.pages.{FaceBookAuthDialog, SignInPage, ContainerWithSigninModulePage, FrontPage}
import com.gu.identity.integration.test.steps.{SignInSteps, UserSteps}
import com.gu.integration.test.steps.{BaseSteps, SocialNetworkSteps}
import org.openqa.selenium.WebDriver


class SocialNetworkTests extends IdentitySeleniumTestSuite {

  /*
  feature("Registration and sign-in using Facebook") {
    scenarioWeb("should be able to register using Facebook") { implicit driver: WebDriver =>
      val facebookUser = SocialNetworkSteps().createNewFacebookTestUser()
      SocialNetworkSteps().goToFacebookAsUser(facebookUser)
      BaseSteps().goToStartPage()
      val registerPage = SignInSteps().clickSignInLink().clickRegisterNewUserLink()
      val authDialog = registerPage.switchToNewSignIn().clickRegisterWithFacebookButton()
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
      val authDialog = registerPage.switchToNewSignIn().clickRegisterWithFacebookButton()
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
      val authDialog = registerPage.switchToNewSignIn().clickRegisterWithFacebookButton()
      authDialog.clickConfirmButton()
      SignInSteps().signOut(frontPage)
      val newLogOutTime = System.currentTimeMillis / 1000 - 24 * 3600
      SignInSteps().setSignOutCookieWithTime(newLogOutTime)
      SocialNetworkSteps().clearLocalStorageFacebookValue()
      BaseSteps().goToStartPage()
      SocialNetworkSteps().checkUserGotAutoSignInBanner(frontPage)
      SignInSteps().checkUserIsLoggedIn(facebookUser.fullName)
      SocialNetworkSteps().deleteFacebookTestUser(facebookUser)
    }

    scenarioWeb("should be asked to re-authenticate when editing profile after logging in with Facebook") { implicit driver: WebDriver =>
      val facebookUser = SocialNetworkSteps().createNewFacebookTestUser()
      SocialNetworkSteps().goToFacebookAsUser(facebookUser)
      BaseSteps().goToStartPage()
      val registerPage = SignInSteps().clickSignInLink().clickRegisterNewUserLink()
      val authDialog = registerPage.switchToNewSignIn().clickRegisterWithFacebookButton()
      authDialog.clickConfirmButton()
      val editAccountDetailsPage = UserSteps().goToEditProfilePage(new ContainerWithSigninModulePage())
      SocialNetworkSteps().checkUserGotReAuthenticationMessage(editAccountDetailsPage)
      val facebookConfirmPasswordDialog = editAccountDetailsPage.clickConfirmWithFacebookButton
      val editProfilePage = facebookConfirmPasswordDialog.enterPassword(facebookUser.password.get).clickContinueButton()
      SocialNetworkSteps().checkUserIsOnEditProfilePage(editProfilePage)
      SocialNetworkSteps().deleteFacebookTestUser(facebookUser)
    }

    scenarioWeb("should stay on Facebook when entering wrong Facebook password during re-authentication") { implicit driver: WebDriver =>
      val facebookUser = SocialNetworkSteps().createNewFacebookTestUser()
      SocialNetworkSteps().goToFacebookAsUser(facebookUser)
      BaseSteps().goToStartPage()
      val registerPage = SignInSteps().clickSignInLink().clickRegisterNewUserLink()
      val authDialog = registerPage.switchToNewSignIn().clickRegisterWithFacebookButton()
      authDialog.clickConfirmButton()
      val editAccountDetailsPage = UserSteps().goToEditProfilePage(new ContainerWithSigninModulePage())
      SocialNetworkSteps().checkUserGotReAuthenticationMessage(editAccountDetailsPage)
      val facebookConfirmPasswordDialog = editAccountDetailsPage.clickConfirmWithFacebookButton
      val editProfilePage = facebookConfirmPasswordDialog.enterPassword("wrongpassword").clickContinueButton()
      SocialNetworkSteps().checkUserIsOnFacebook()
      SocialNetworkSteps().deleteFacebookTestUser(facebookUser)
    }

      scenarioWeb("should be asked to re-request e-mail permissions after denying them the first time") { implicit driver: WebDriver =>
        val facebookUser = SocialNetworkSteps().createNewFacebookTestUser()
        SocialNetworkSteps().goToFacebookAsUser(facebookUser)
        BaseSteps().goToStartPage()
        val registerPage = SignInSteps().clickSignInLink().clickRegisterNewUserLink()
        val authDialog = registerPage.switchToNewSignIn().clickRegisterWithFacebookButton()
        authDialog.clickEditInformationProvided().clickEmailCheckBox().clickConfirmButton()
        SocialNetworkSteps().checkUserGotFacebookEmailError(registerPage)
        SignInSteps().checkUserIsNotLoggedIn(facebookUser.fullName)
        val signinPage = new SignInPage()
        signinPage.clickFaceBookSignInButton(false)
        val requestPermissionsDialog = new FaceBookAuthDialog()
        requestPermissionsDialog.clickConfirmButton()
        BaseSteps().goToStartPage()
        SignInSteps().checkUserIsLoggedIn(facebookUser.fullName)
        SocialNetworkSteps().deleteFacebookTestUser(facebookUser)
      }

    scenarioWeb("should be asked to re-authenticate when editing profile after logging in with Google") { implicit driver: WebDriver =>
      BaseSteps().goToStartPage()
      SignInSteps().signInUsingGoogle()
      val editAccountDetailsPage = UserSteps().goToEditProfilePage(new ContainerWithSigninModulePage())
      SocialNetworkSteps().checkUserGotReAuthenticationMessage(editAccountDetailsPage)
      val googleConfirmPasswordDialog = editAccountDetailsPage.clickConfirmWithGoogleButton
      val editProfilePage = googleConfirmPasswordDialog.clickChooseAccountButton()
      SocialNetworkSteps().checkUserIsOnEditProfilePage(editProfilePage)
    }
  }
  */
}
