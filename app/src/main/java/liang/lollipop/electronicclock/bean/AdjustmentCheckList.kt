package liang.lollipop.electronicclock.bean

import liang.lollipop.electronicclock.utils.CheckListDialog

/**
 * @author lollipop
 * @date 2020-01-05 17:38
 * 多选列表的偏好设置项
 */
class AdjustmentCheckList: AdjustmentInfo() {

    val unselectedList = ArrayList<CheckListDialog.Info>()
    val selectedList = ArrayList<CheckListDialog.Info>()

    var maxSize = 0

    fun add(name: String, id: Int) {
        if (isSelected(id)) {
            return
        }
        unselectedList.add(CheckListDialog.Info(name, id))
    }

    fun selected(id: Int) {
        unselectedList.find(id)?.let {
            unselectedList.remove(it)
            selectedList.add(it)
        }
        while (maxSize > 0 && selectedList.size > maxSize) {
            val info = selectedList.removeAt(0)
            unselectedList.add(info)
        }
    }

    fun onSelectedChange(selected: ArrayList<CheckListDialog.Info>) {
        unselectedList.addAll(selectedList)
        selectedList.clear()
        selectedList.addAll(selected)
        for (info in selected) {
            unselectedList.remove(info)
        }
        while (maxSize > 0 && selectedList.size > maxSize) {
            val info = selectedList.removeAt(0)
            unselectedList.add(info)
        }
    }

    private fun isSelected(id: Int): Boolean {
        if (selectedList.isEmpty()) {
            return false
        }
        for (info in selectedList) {
            if (info.id == id) {
                return true
            }
        }
        return false
    }

    private fun ArrayList<CheckListDialog.Info>.find(id: Int): CheckListDialog.Info? {
        if (this.isEmpty()) {
            return null
        }
        for (info in this) {
            if (info.id == id) {
                return info
            }
        }
        return null
    }

}