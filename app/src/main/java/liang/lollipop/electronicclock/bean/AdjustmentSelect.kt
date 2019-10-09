package liang.lollipop.electronicclock.bean

/**
 * @author lollipop
 * @date 2019-10-08 20:33
 */
class AdjustmentSelect(run: AdjustmentSelect.() -> Unit):
    AdjustmentInfo() {

    val itemList = ArrayList<String>()

    var selectedIndex = -1

    fun addItem(vararg items: String) {
        itemList.addAll(items)
    }

    fun selectBy(value: String) {
        for (index in itemList.indices) {
            if (itemList[index] == value) {
                selectedIndex = index
            }
        }
    }

}