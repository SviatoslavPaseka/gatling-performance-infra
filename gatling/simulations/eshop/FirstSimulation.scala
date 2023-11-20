package eshop

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.JsonFormatter

class FirstSimulation extends Simulation {

  private val httpProtocol = http
    .baseUrl("http://localhost:8888")
  val headers_general = Map(
    HttpHeaderNames.ContentType -> HttpHeaderValues.ApplicationJson,
    HttpHeaderNames.Accept -> HttpHeaderValues.ApplicationJson
  )

  private val scn = scenario("FirstSimulation")
    .exec(
      http("home page")
        .get("/eshop")
        .headers(headers_general)
        .check(
          status is 200
        ).check(bodyString.saveAs("responseBody"))
    ).exec(session => {
      val responseBody = session("responseBody").as[String] // Retrieve the response body from the session
      println(s"------Response Body /eshop: ------\n" + JsonFormatter.formatJson(responseBody)) // Print the response body
      session
    }).exec(
      http("category all")
        .get("/eshop/control/category/all")
        .headers(headers_general)
        .check(
          status is 200,
          jsonPath("$.categories").exists,
          jsonPath("$.categories").notNull,
          jsonPath("$.categories[*]").count.is(6),
          jsonPath("$..id").findAll.saveAs("categories")
        ).check(bodyString.saveAs("responseBody"))
    ).exec(session => {
      val responseBody = session("responseBody").as[String] // Retrieve the response body from the session
      println(s"------Response Body /eshop/control/category/all: ------\n" + JsonFormatter.formatJson(responseBody)) // Print the response body
      session
    }).exec(
      http("category-2")
        .get("/eshop/control/category/2/products")
        .headers(headers_general)
        .check(
          status is 200,
          jsonPath("$..id").findAll.saveAs("products"),
        ).check(bodyString.saveAs("responseBody"))
    ).exec(session => {
      val responseBody = session("responseBody").as[String] // Retrieve the response body from the session
      println(s"------Response Body /eshop/control/category/2/products: ------\n" + JsonFormatter.formatJson(responseBody)) // Print the response body

      session
    }).exec(
      http("additem-6")
        .post("/eshop/control/cart/additem")
        .body(StringBody("""{"id": 6}""")).asJson
        .headers(headers_general)
        .check(
          status is 200
        ).check(bodyString.saveAs("responseBody"))
    ).exec(session => {
      val responseBody = session("responseBody").as[String] // Retrieve the response body from the session
      println(s"------Response Body /eshop/control/cart/additem: ------\n" + JsonFormatter.formatJson(responseBody)) // Print the response body

      session
    }).exec(
      http("additem-3")
        .post("/eshop/control/cart/additem")
        .body(StringBody("""{"id": 3}""")).asJson
        .headers(headers_general)
        .check(
          status is 200
        ).check(bodyString.saveAs("responseBody"))
    ).exec(
      http("getInfo")
        .get("/eshop/control/cart/getInfo")
        .headers(headers_general)
        .check(
          status is 200,
          jsonPath("$.cartInfo.cartItems..id").ofType[Int].findAll.saveAs("productsInCart"),
          jmesPath("cartInfo.cartItems[*].[product.id,quantity]").saveAs("IdQuantity")
        )
        .check(bodyString.saveAs("responseBody"))
    ).exec(session => {
      val responseBody = session("responseBody").as[String] // Retrieve the response body from the session
      println(s"------Response Body /eshop/control/cart/getInfo: ------\n" + JsonFormatter.formatJson(responseBody)) // Print the response body

      session
    }).exec(
      session =>
        session
          .set("quantity", 5)
          .set("selectedProductForUpdateId", 6)
    ).exec(
      http("update product 6")
        .post("/eshop/control/cart/updatequantity")
        .body(StringBody("""{"id": 6, "quantity": 5}""")).asJson
        .headers(headers_general)
        .check(
          status is 200,
          jsonPath(session => "$.cartInfo.cartItems[?(@.product.id=="
            + session("selectedProductForUpdateId").as[String]
            + ")].quantity").is(session => session("quantity").as[String])
        ).check(bodyString.saveAs("responseBody"))
    ).exec(session => {
      val responseBody = session("responseBody").as[String] // Retrieve the response body from the session
      println(s"------Response Body /eshop/control/cart/updatequantity: ------\n" + JsonFormatter.formatJson(responseBody)) // Print the response body

      session
    }).exec(
      session =>
        session
          .set("selectedProductForRemoveId", 3)
    ).exec(
      http("remove-product 3")
        .delete("/eshop/control/cart/removeitem")
        .body(StringBody("""{"id": 3}"""))
        .headers(headers_general)
        .check(
          status is 200,
          jsonPath(session => "$.removedProductId").is(session => session("selectedProductForRemoveId").as[String]),
          jsonPath("$.cartInfo.cartItems[*].product.id").findAll.transform(ids => ids.exists(_ == 3)).is(false) // Check if 3 is not present in idList
        ).check(bodyString.saveAs("responseBody"))
    ).exec(session => {
    val responseBody = session("responseBody").as[String] // Retrieve the response body from the session
    println(s"------Response Body /eshop/control/cart/removeitem: ------\n" + JsonFormatter.formatJson(responseBody)) // Print the response body

    session
  }).exec(
      http("get cart Info")
        .get("/eshop/control/cart/getInfo")
        .headers(headers_general)
        .check(
          status is 200,
          jsonPath("$.cartInfo.cartItems..id").ofType[Int].findAll.saveAs("productsInCart"),
          jmesPath("cartInfo.cartItems[*].[product.id,quantity]").saveAs("IdQuantity")
        )
        .check(bodyString.saveAs("responseBody"))
    ).exec(session => {
      val responseBody = session("responseBody").as[String] // Retrieve the response body from the session
      println(s"------Response Body /eshop/control/cart/getInfo: ------\n" + JsonFormatter.formatJson(responseBody)) // Print the response body

      session
    }).exec(
      session =>
        session
          .set("name", "namename")
          .set("email", "namename")
          .set("phone", "123123123")
          .set("address", "namename")
          .set("ccNumber", "1239012903")
    ).exec(
      http("purchase-submit")
        .post("/eshop/control/checkout/purchase/submit")
        .body(StringBody("""{"name": "${name}","email": "${email}","phone": "${phone}","address": "${address}","ccNumber": "{$ccNumber}"}""")).asJson
        .headers(headers_general)
        .check(
          status is 200
        )
        .check(
          bodyString.saveAs("rb")
        )
    ).exec(session => {
      val responseBody = session("rb").as[String] // Retrieve the response body from the session
      println(s"------Response Body /eshop/control/checkout/purchase/submit: ------\n" + JsonFormatter.formatJson(responseBody)) // Print the response body
      session
    })

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}