/*
 * MIT License
 *
 * Copyright (c) 2021 2Play Technologies Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.twoplaylabs.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/*
    Author: Damjan Miloshevski 
    Created on 09/07/2021
    Project: betting-doctor
*/
object GsonUtil {
    private fun getGson(): Gson {
        return Gson()
    }

    fun <T> deserialize(clazz: Class<T>, json: String): T {
        return getGson().fromJson(json, clazz)
    }

    fun <T> serialize(clazz: Class<T>, model: T): String {
        return getGson().toJson(model, clazz)
    }
    fun <T> serializeListToJson(list:List<T>):String{
        val listType = object : TypeToken<List<T>>() {}.type
        return  getGson().toJson(list, listType)
    }
    fun <T> deserializeListToJson(json: String):List<T>{
        val listType = object : TypeToken<List<T>>() {}.type
        return  getGson().fromJson(json,listType)
    }
}