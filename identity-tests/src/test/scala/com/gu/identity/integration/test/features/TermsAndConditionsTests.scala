package com.gu.identity.integration.test.features

import com.gu.automation.support.Config
import com.gu.identity.integration.test.IdentitySeleniumTestSuite
import com.gu.identity.integration.test.steps.{TermsAndConditionsSteps, UserSteps}
import com.gu.identity.integration.test.tags.{CoreTest, Unstable}
import com.gu.identity.integration.test.util.{UrlParamExtractor, User}
import com.gu.integration.test.steps.BaseSteps
import org.openqa.selenium.WebDriver

class TermsAndConditionsTests extends IdentitySeleniumTestSuite {


  feature("T&C's feature") {
    scenarioWeb("should be shown third party T&C's when logging-in from jobs", CoreTest) { implicit driver: WebDriver =>
      val signinPage = TermsAndConditionsSteps().goToJobsSite.clickLogin()
      TermsAndConditionsSteps().checkJobsTermsVisible(signinPage)
    }

    scenarioWeb("should be shown third party T&C's when registering from jobs", CoreTest) { implicit  driver: WebDriver =>
      val registerPage = TermsAndConditionsSteps().goToJobsSite.clickRegister()
      TermsAndConditionsSteps().checkJobsTermsVisible(registerPage)
    }

    scenarioWeb("should have agree page as return URL for Facebook sign in", CoreTest) { implicit driver: WebDriver =>
      val signinPage = TermsAndConditionsSteps().goToJobsSite.clickLogin()
      UrlParamExtractor.returnUrl(signinPage.getFaceBookSignInLink).get.getPath should be("/agree/GRS")
    }

    scenarioWeb("should have agree page as return URL for Google sign in", CoreTest) { implicit driver: WebDriver =>
      val signinPage = TermsAndConditionsSteps().goToJobsSite.clickLogin()
      UrlParamExtractor.returnUrl(signinPage.getGoogleSignInLink).get.getPath should be("/agree/GRS")
    }

    scenarioWeb("should have agree page as return URL for Facebook registration", CoreTest) { implicit driver: WebDriver =>
      val registerPage = TermsAndConditionsSteps().goToJobsSite.clickRegister()
      UrlParamExtractor.returnUrl(registerPage.getFaceBookSignInLink).get.getPath should be("/agree/GRS")
    }

    scenarioWeb("should have agree page as return URL for Google registration", CoreTest) { implicit driver: WebDriver =>
      val registerPage = TermsAndConditionsSteps().goToJobsSite.clickRegister()
      UrlParamExtractor.returnUrl(registerPage.getGoogleSignInLink).get.getPath should be("/agree/GRS")
    }

    scenarioWeb("should show third party T&C's to already logged in user", CoreTest) { implicit driver: WebDriver =>
      val user: User = UserSteps().createRandomBasicUser().right.get
      val agreePage = TermsAndConditionsSteps().goToJobsSite.clickLoginAsExistingUser()
      TermsAndConditionsSteps().checkJobsTermsVisible(agreePage)
    }
  }

}
