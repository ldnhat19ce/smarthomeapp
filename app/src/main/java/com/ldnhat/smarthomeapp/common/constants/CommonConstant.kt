package com.ldnhat.smarthomeapp.common.constants

class CommonConstant {
    companion object {
        const val DATA_PLANE_ACTION = "data-plane"
        const val TAG = "gatt-service"
        const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
        const val LOCATION_PERMISSION_REQUEST_CODE = 2
        const val BLUETOOTH_ALL_PERMISSIONS_REQUEST_CODE = 3
        const val SERVICE_UUID = "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
        const val CHAR_FOR_READ_UUID = "1c95d5e3-d8f7-413a-bf3d-7a2e5d7be87e"
        const val CHAR_FOR_WRITE_UUID = "1c95d5e3-d8f7-413a-bf3d-7a2e5d7be87e"
        const val CHAR_FOR_INDICATE_UUID = "1c95d5e3-d8f7-413a-bf3d-7a2e5d7be87e"
        const val CHAR_FOR_INDICATE_TEMP_UUID = "1c95d5e3-d8f7-413a-bf3d-7a2e5d7be87e"
        const val CHAR_FOR_INDICATE_HUMIDIY_UUID = "f639b61e-f796-11ed-b67e-0242ac120002"
        const val CHAR_FOR_INDICATE_LIGHT_UUID = "beb5483e-36e1-4688-b7f5-ea07361b26a8"
        const val CHAR_FOR_WRITE_LIGHT_UUID = "beb5483e-36e1-4688-b7f5-ea07361b26a8"
        const val CCC_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb"
    }
}