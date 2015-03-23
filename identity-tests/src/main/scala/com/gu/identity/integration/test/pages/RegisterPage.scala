package com.gu.identity.integration.test.pages

import com.gu.integration.test.util.ElementLoader._
import com.gu.integration.test.util.WebElementEnhancer._
import org.openqa.selenium.{By, JavascriptExecutor, WebDriver, WebElement}

class RegisterPage(implicit driver: WebDriver) extends UserFormPage {
  private def emailInputField: WebElement = findByTestAttribute("reg-email")
  private def userNameInputField: WebElement = findByTestAttribute("reg-username")
  private def firstNameInputField: WebElement = findByTestAttribute("reg-first-name")
  private def lastNameInputField: WebElement = findByTestAttribute("reg-second-name")
  private def pwdInputField: WebElement = findByTestAttribute("reg-pwd")
  private def createButton: WebElement = findByTestAttribute("create-user-button")
  private def registerWithFacebookButton: WebElement = findByTestAttribute("facebook-sign-in")

  def enterEmail(email: String) = {
    emailInputField.sendKeys(email)
    this
  }

  def enterPwd(pwd: String) = {
    pwdInputField.sendKeys(pwd)
    this
  }

  def enterUsername(userName: String) = {
    userNameInputField.sendKeys(userName)
    this
  }

  def enterFirstName(firstName: String) = {
    firstNameInputField.sendKeys(firstName)
    this
  }

  def enterLastName(lastName: String) = {
    lastNameInputField.sendKeys(lastName)
    this
  }

  def clickCreateUser() = {
    createButton.scrollIntoView()
    createButton.click()
    this
  }

  def clickRegisterWithFacebookButton(): FaceBookAuthDialog = {
    driver.asInstanceOf[JavascriptExecutor].executeScript("arguments[0].scrollIntoView(true);", registerWithFacebookButton);
    Thread sleep 500
    registerWithFacebookButton.click()
    new FaceBookAuthDialog()
  }

  def getFormErrorText(): Option[String] = {
    try {
      Some(driver.findElement(By.xpath("//div[@class='form__error']")).getText())
    } catch {
      case _: org.openqa.selenium.NoSuchElementException => None
    }
  }
}
