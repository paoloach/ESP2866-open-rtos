cmake_minimum_required(VERSION 3.10)
project(__{project_name}__ )

set(SOURCE_DIR ${RTOS_DIR}/components/lwip)



file(READ ../include/sdkconfig.h SDKCONFIG)
string(FIND ${SDKCONFIG} "ENABLE_MDNS" FOUND)
if (FOUND GREATER 1)
    add_library(mdns STATIC
        ${SOURCE_DIR}/src/mdns.c
        ${SOURCE_DIR}/src/mdns_console.c
        ${SOURCE_DIR}/src/mdns_networking.c
        )


    target_compile_options(mdns  PRIVATE  -DMBEDTLS_CONFIG_FILE="mbedtls/esp_config.h" ${COMMON_FLAGS} ${WARNING_FLAGS} )

    add_dependencies(mdns lwip mbedtls console tcpip_adapter )

    target_include_directories(mdns PRIVATE
        ${SOURCE_DIR}/private_include
        )

    target_include_directories(mdns PUBLIC
        ${SOURCE_DIR}/include
        ${RTOS_DIR}/components/heap/include
        ${RTOS_DIR}/components/heap/port/esp8266/include
        ${RTOS_DIR}/components/lwip/include
        ${RTOS_DIR}/components/lwip/include/lwip/apps
        ${RTOS_DIR}/components/lwip/lwip/src/include
        ${RTOS_DIR}/components/lwip/port/esp8266/include
        ${RTOS_DIR}/components/lwip/lwip/src/include/posix
        ${RTOS_DIR}/components/bootloader_support/include
        ${RTOS_DIR}/components/cjson/include
        ${RTOS_DIR}/components/cjson/cJSON
        ${RTOS_DIR}/components/esp8266/include
        ${RTOS_DIR}/components/freertos/include
        ${RTOS_DIR}/components/freertos/include/freertos
        ${RTOS_DIR}/components/freertos/include/freertos/private
        ${RTOS_DIR}/components/freertos/port/esp8266/include
        ${RTOS_DIR}/components/freertos/port/esp8266/include/freertos
        ${RTOS_DIR}/components/jsmn/include
        ${RTOS_DIR}/components/log/include
        ${RTOS_DIR}/components/mqtt/include
        ${RTOS_DIR}/components/mqtt/paho/MQTTClient-C/src
        ${RTOS_DIR}/components/mqtt/paho/MQTTClient-C/src/FreeRTOS
        ${RTOS_DIR}/components/mqtt/paho/MQTTPacket/src
        ${RTOS_DIR}/components/newlib/include
        ${RTOS_DIR}/components/newlib/newlib/include
        ${RTOS_DIR}/components/newlib/newlib/port/include
        ${RTOS_DIR}/components/nvs_flash/include
        ${RTOS_DIR}/components/smartconfig_ack/include
        ${RTOS_DIR}/components/spi_flash/include
        ${RTOS_DIR}/components/spiffs/include
        ${RTOS_DIR}/components/spiffs/include/spiffs
        ${RTOS_DIR}/components/ssl/include
        ${RTOS_DIR}/components/ssl/mbedtls/mbedtls/include
        ${RTOS_DIR}/components/ssl/mbedtls/port/esp8266/include
        ${RTOS_DIR}/components/ssl/mbedtls/port/openssl/include
        ${RTOS_DIR}/components/tcpip_adapter/include
        ${RTOS_DIR}/components/util/include
        ${SOURCE_DIR}/port/esp8266/netif
        ${CMAKE_SOURCE_DIR}/include
        )
else(FOUND GREATER 1)
    add_library(mdns INTERFACE )
endif()