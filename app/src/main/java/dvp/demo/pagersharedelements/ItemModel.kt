package dvp.demo.pagersharedelements

data class ItemModel(val text: String, var isSelected: Boolean = false)

object Data {
    fun fakeData(): List<ItemModel> {
        return (1..50).map { ItemModel("$it") }
    }
}