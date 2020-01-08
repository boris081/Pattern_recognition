package com.example.pattern_recognition.mysql

import android.util.Log
import java.sql.*


class MysqlCon  {

    var mysql_ip = "192.168.1.24"
//    var mysql_ip = "192.168.43.248"
    var mysql_port = 3306 // Port 預設為 3306

    var db_name = "django_eq_it"
    var url = "jdbc:mysql://$mysql_ip:$mysql_port/$db_name"
    var db_user = "user"
    var db_password = "abc123"

    fun run() {
        try {
            Class.forName("com.mysql.jdbc.Driver")
            Log.v("DB", "加載驅動成功")
        } catch (e: ClassNotFoundException) {
            Log.v("DB", "加載驅動失敗")
            return
        }
        // 連接資料庫
        try {
            DriverManager.getConnection(url, db_user, db_password)
            Log.v("DB", "遠端連接成功")
        } catch (e: SQLException) {
            Log.v("DB", "遠端連接失敗")
            Log.v("DB", e.toString())
            return
        }
    }

    fun getData(): String? {
        var data = ""
        try {
            val con: Connection = DriverManager.getConnection(url, db_user, db_password)
            val sql = "SELECT * FROM eq_it_data"
            val st: Statement = con.createStatement()
            val rs: ResultSet = st.executeQuery(sql)
            while (rs.next()) {
                val id: String = rs.getString("NAME")
                val ig: String = rs.getString("Industrial_gloves")
                val ig_ok: String = rs.getString("Industrial_gloves_ok")
                val ih: String = rs.getString("Industrial_helmet")
                val ih_ok: String = rs.getString("Industrial_helmet_ok")
                val sh: String = rs.getString("safety_harness")
                val sh_ok: String = rs.getString("safety_harness_ok")
                val sl: String = rs.getString("slippers")
                val sl_ok: String = rs.getString("slippers_ok")
                val vs: String = rs.getString("vest")
                val vs_ok: String = rs.getString("vest_ok")

                data += "$id  $ig  $ig_ok  $ih  $ih_ok  $sh  $sh_ok  $sl  $sl_ok  $vs $vs_ok \n"
            }
            st.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return data
    }

    fun getString(string:String): String? {
        val con: Connection = DriverManager.getConnection(url, db_user, db_password)
        val sql = "SELECT * FROM eq_it_data"
        val st: Statement = con.createStatement()
        val rs: ResultSet = st.executeQuery(sql)
        return rs.getString(string)

    }
}
