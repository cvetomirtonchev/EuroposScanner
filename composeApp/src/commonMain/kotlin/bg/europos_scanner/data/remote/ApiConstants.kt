package bg.europos_scanner.data.remote

object ApiConstants {
    const val BASE_URL = "http://192.168.1.19:8080"

    //    const val BASE_URL = "https://europos.bg"
    const val LOGIN = "/api/auth/login"
    const val STUDENTS = "/api/children/moderator/all"
    const val ORDERS = "/api/order/moderator/all"
    const val CHANGE_ORDER_STATUS = "/api/order/moderator/change-status"
    const val USER_DETAILS = "/api/user/details"
}
