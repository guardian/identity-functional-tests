package com.gu.identity.integration.test.tags

import org.scalatest.Tag

  /* Usage: The tag is added as a comma separated list for each scenario after the scenario name
  e.g. scenarioWeb("Example test that is social", SocialTest)
    Running tests locally:  The Run/Debug configuration can specify which tags you want to run or ignore using the tag
    text e.g. to run only core tests but ignore optional tests you would use the following in Test Options
    -n CoreTest -l OptionalTest -l SocialTest
    Running tests TeamCity: In Edit Configuration Settings -> Build Step -> SBT Commands enter the following
    "project identity-tests" clean compile "test-only -- -l SocialTest"
    to add further tags use this format "test-only -- -l SocialTest -l OptionalTest"
    NOTE: Ideally you should not use -n as this will ignore tests that are untagged. By only using -l these untagged
    tests will also be run
  */

  //test is essential and the site will not function without this feature
  object CoreTest extends Tag("CoreTest")

  //test is important but the site can handle this feature missing for a short time
  object OptionalTest extends Tag("OptionalTest")

  //test is specifically testing the interaction with external social applications
  object SocialTest extends Tag("SocialTest")

