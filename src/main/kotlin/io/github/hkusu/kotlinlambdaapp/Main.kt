package io.github.hkusu.kotlinlambdaapp

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler

class Main: RequestHandler<Int, String> {

    override fun handleRequest(input: Int?, context: Context?): String {
        val lambdaLogger = context?.logger
        lambdaLogger?.log("count = " + input)
        return "${(input ?: 1) * 7}"
    }
}
