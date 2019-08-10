package liang.lollipop.widget.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import liang.lollipop.widget.widget.PanelAdapter
import liang.lollipop.widget.widget.PanelInfo
import org.json.JSONObject
import java.lang.RuntimeException

/**
 * @author lollipop
 * @date 2019-08-07 15:04
 * 数据库操作类
 */
class DatabaseHelper private constructor(context: Context):
    SQLiteOpenHelper(context,DB_NAME,null, VERSION)  {

    companion object {
        private const val DB_NAME = "WidgetDatabase"
        private const val VERSION = 1

        fun read(context: Context): SqlDB {
            return SqlDB(DatabaseHelper(context).readableDatabase)
        }

        fun write(context: Context): SqlDB {
            return SqlDB(DatabaseHelper(context).writableDatabase)
        }

    }

    private object WidgetTable {
        const val TABLE = "WidgetTable"
        const val ID = "WIDGET_ID"
        const val TYPE_NAME = "TYPE_NAME"
        const val INFO = "INFO"
        const val DIRECTION = "DIRECTION"
        const val PAGE_NUMBER = "PAGE_NUMBER"

        const val ALL = "$ID " +
                ", $TYPE_NAME " +
                ", $INFO " +
                ", $DIRECTION " +
                ", $PAGE_NUMBER"

        const val SELECT_ONE_PAGE = " select $ALL " +
                " from $TABLE " +
                " WHERE $DIRECTION = ? and $PAGE_NUMBER = ? ;"

        const val CREATE_TABLE = "create table $TABLE ( " +
                " $ID INTEGER PRIMARY KEY " +
                " , $TYPE_NAME INTEGER " +
                " , $INFO INTEGER " +
                " , $DIRECTION VARCHAR " +
                " , $PAGE_NUMBER INTEGER " +
                " );"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(WidgetTable.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    class SqlDB (private val database: SQLiteDatabase) {
        var isClose = false
            private set

        private val db: SQLiteDatabase
            get() {
                if (isClose) {
                    throw RuntimeException("SQLiteDatabase is closed")
                }
                return database
            }

        fun getOnePage(direction: String, pageNumber: Int = 0,
                       panelList: ArrayList<PanelInfo>): SqlDB {
            val c = db.rawQuery(WidgetTable.SELECT_ONE_PAGE, arrayOf(direction, "$pageNumber"))
            while (c.moveToNext()) {
                panelList.add(newInfo(c))
            }
            c.close()
            return this
        }

        fun install(info: PanelInfo, direction: String, pageNumber: Int = 0,
                    result: ((Long) -> Unit)? = null): SqlDB {
            val value = db.insert(WidgetTable.TABLE, "",
                ContentValues().putData(info, direction, pageNumber))
            result?.invoke(value)
            return this
        }

        fun update(info: PanelInfo, direction: String, pageNumber: Int = 0,
                   result: ((Int) -> Unit)? = null): SqlDB {
            val value = db.update(WidgetTable.TABLE, ContentValues().putData(info, direction, pageNumber),
                " ${WidgetTable.ID} = ? ", arrayOf("${info.id}"))
            result?.invoke(value)
            return this
        }

        fun delete(id: Int,
                   result: ((Int) -> Unit)? = null): SqlDB {
            val value = db.delete(WidgetTable.TABLE, " ${WidgetTable.ID} = ? ", arrayOf("$id"))
            result?.invoke(value)
            return this
        }

        // 开启一次事务
        fun transaction(run: (SqlDB.() -> Unit)) {
            // 由于批量操作，因此开启事务并且try执行，
            // 由于事务提交本身存在消耗，因此可以提交速度，并且避免损失
            try {
                db.beginTransaction()
                run(this)
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }

        private fun newInfo(c: Cursor): PanelInfo {
            val type = c.getStringByName(WidgetTable.TYPE_NAME)
            val info = PanelAdapter.newInfo(type)
            info.id = c.getIntByName(WidgetTable.ID)
            info.parse(JSONObject(c.getStringByName(WidgetTable.INFO)))
            return info
        }

        private fun ContentValues.putData(panelInfo: PanelInfo, direction: String, pageNumber: Int = 0): ContentValues {
            clear()
            put(WidgetTable.TYPE_NAME, PanelAdapter.className(panelInfo))
            val obj = JSONObject()
            panelInfo.serialize(obj)
            put(WidgetTable.INFO, obj.toString())
            put(WidgetTable.DIRECTION, direction)
            put(WidgetTable.PAGE_NUMBER, pageNumber)
            return this
        }

        private inline fun <reified T> Cursor.getValueByName(name: String, def: T): T {
            val index = getColumnIndex(name)
            if (index < 0) {
                return def
            }
            return when (def) {
                is Int -> getInt(index) as T
                is String -> getString(index) as T
                is Double -> getDouble(index) as T
                else -> def
            }
        }

        fun close() {
            if (isClose) {
                return
            }
            db.close()
            isClose = true
        }

        private fun Cursor.getIntByName(name: String, def: Int = 0): Int {
            return getValueByName(name, def)
        }

        private fun Cursor.getStringByName(name: String, def: String = ""): String {
            return getValueByName(name, def)
        }

    }

}