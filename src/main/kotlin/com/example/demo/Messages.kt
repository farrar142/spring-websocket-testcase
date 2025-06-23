package com.example.demo

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class HelloMessage(
    val name: String
)


//Test에서 StompSessionHandlerAdapter가 json -> Greeting으로 변환하기 위해서 @JsonCreator를 추가해야됨
class Greeting @JsonCreator constructor(
    @JsonProperty("content") val content: String
)
