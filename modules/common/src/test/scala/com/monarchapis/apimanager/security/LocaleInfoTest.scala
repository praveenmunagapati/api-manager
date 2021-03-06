/*
 * Copyright (C) 2015 CapTech Ventures, Inc.
 * (http://www.captechconsulting.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.monarchapis.apimanager.security

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class LocaleInfoTest extends FlatSpec with Matchers {
  behavior of "LocaleInfo"

  it should "display the locale in {language}-{country}-{variant} format" in {
    LocaleInfo(language = "en")() should be("en")
    LocaleInfo(language = "en", country = Some("us"))() should be("en-us")
    LocaleInfo(language = "en", variant = Some("test"))() should be("en-test")
    LocaleInfo(language = "en", country = Some("us"), variant = Some("test"))() should be("en-us-test")
  }
}