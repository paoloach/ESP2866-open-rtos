cmake_minimum_required(VERSION 3.10)
project(__{project_name}__ )

set(SOURCE_DIR ${RTOS_DIR}/components/esp8266)

add_library(esp8266 STATIC
        ${SOURCE_DIR}/driver/hw_timer.c
        ${SOURCE_DIR}/driver/spi_interface.c
        ${SOURCE_DIR}/driver/uart.c
        ${SOURCE_DIR}/driver/gpio.c
        ${SOURCE_DIR}/driver/i2c_master.c
        ${SOURCE_DIR}/source/event_default_handlers.c
        ${SOURCE_DIR}/source/system_api.c
        ${SOURCE_DIR}/source/phy_init.c
        ${SOURCE_DIR}/source/ets_printf.c
        ${SOURCE_DIR}/source/esp_wifi_os_adapter.c
        ${SOURCE_DIR}/source/esp_wifi.c
        ${SOURCE_DIR}/source/event_loop.c
        ${SOURCE_DIR}/source/esp_socket.c
        ${SOURCE_DIR}/source/startup.c
        ${SOURCE_DIR}/source/chip_boot.c)

add_library(esp8266_boot STATIC ${SOURCE_DIR}/source/ets_printf)


target_compile_options(esp8266 PRIVATE -DMEMLEAK_DEBUG ${COMMON_FLAGS} ${WARNING_FLAGS})
target_compile_options(esp8266_boot PRIVATE -DMEMLEAK_DEBUG -DBOOTLOADER_BUILD=1 ${COMMON_FLAGS} ${WARNING_FLAGS})

target_include_directories(esp8266_boot PUBLIC
        ${CMAKE_SOURCE_DIR}/include
        ${RTOS_DIR}/components/esp8266/include
        ${RTOS_DIR}/components/util/include
        ${RTOS_DIR}/components/esp8266/include
        ${RTOS_DIR}/components/bootloader/subproject/main/include
        ${RTOS_DIR}/components/bootloader_support/include
        ${RTOS_DIR}/components/bootloader_support/include_priv
        ${RTOS_DIR}/components/spi_flash/include
        ${RTOS_DIR}/components/log/include
        ${RTOS_DIR}/components/util/include
        ${SOURCE_DIR}/source
        )

target_include_directories(esp8266 PUBLIC
        ${CMAKE_SOURCE_DIR}/include
        ${RTOS_DIR}/components/esp8266/include
        ${RTOS_DIR}/components/util/include
        ${RTOS_DIR}/components/bootloader/subproject/main/include
        ${RTOS_DIR}/components/bootloader_support/include
        ${RTOS_DIR}/components/bootloader_support/include_priv
        ${RTOS_DIR}/components/spi_flash/include
        ${RTOS_DIR}/components/log/include
        ${RTOS_DIR}/components/esp8266/include
        ${RTOS_DIR}/components/util/include
        ${RTOS_DIR}/components/freertos/port/esp8266/include
        ${RTOS_DIR}/components/freertos/port/esp8266/include/freertos
        ${RTOS_DIR}/components/freertos/include
        ${RTOS_DIR}/components/freertos/include/freertos
        ${RTOS_DIR}/components/freertos/include/freertos/private
        ${RTOS_DIR}/components/lwip/lwip/src/include
        ${RTOS_DIR}/components/lwip/port/esp8266/include
        ${RTOS_DIR}/components/tcpip_adapter/include
        ${RTOS_DIR}/components/lwip/include/lwip/apps
        ${RTOS_DIR}/components/nvs_flash/include
        ${RTOS_DIR}/components/newlib/newlib/port/include
        ${SOURCE_DIR}/include
        ${SOURCE_DIR}/include/driver
        )