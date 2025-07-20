cmake_minimum_required(VERSION 3.22.1)
project("app")

add_library(${CMAKE_PROJECT_NAME} SHARED
        app.cpp
)

target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log
)
