package liang.lollipop.electronicclock.bean

/**
 * @author lollipop
 * @date 2019-10-08 20:33
 */
class AdjustmentSelect: AdjustmentInfo() {

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

    fun selectedItem(): String {
        if (itemList.isEmpty() || selectedIndex < 0 || selectedIndex >= itemList.size) {
            return ""
        }
        return itemList[selectedIndex]
    }

}