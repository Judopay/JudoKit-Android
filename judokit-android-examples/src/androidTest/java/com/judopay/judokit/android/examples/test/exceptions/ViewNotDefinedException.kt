package com.judopay.judokit.android.examples.test.exceptions

class ViewNotDefinedException(where: String?, view: String) :
    Exception("$where. View \"$view\" not defined.")
