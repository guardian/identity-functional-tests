package com.gu.identity.integration.test.pages

import com.gu.integration.test.pages.common.ParentPage
import com.gu.integration.test.util.ElementLoader._
import org.openqa.selenium.{By, WebDriver, WebElement}

class EmailVerificationPage(implicit driver: WebDriver) extends ParentPage {
  private def completeRegistrationButton: WebElement = findByDataLinkAttribute("Continue")

  def getCompleteRegistrationButtonLink: String = {
    completeRegistrationButton.getAttribute("href")
  }

}
