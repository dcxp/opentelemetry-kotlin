package io.opentelemetry.kotlin.api

class EqualsTester {
    val groups: MutableList<List<Any>> = mutableListOf()

    fun addEqualityGroup(vararg objects: Any) {
        groups.add(objects.toList())
    }

    fun testEquals() {
        checkGroupEquality()
        checkInterGroupInequality()
    }

    fun checkGroupEquality() {
        for (group in groups) {
            for (element1 in group) {
                for (element2 in group) {
                    require(element1 == element2) { "$element1 is not equal to $element2" }
                    require(element1.hashCode() == element2.hashCode()) {
                        "$element1 hashcode is not equal to $element2 hashcode"
                    }
                }
            }
        }
    }
    fun checkInterGroupInequality() {
        for (group1 in groups) {
            for (group2 in groups) {
                if (group1 === group2) {
                    continue
                }
                for (element1 in group1) {
                    for (element2 in group2) {
                        require(element1 != element2) { "$element1 is equal to $element2" }
                        require(element1.hashCode() != element2.hashCode()) {
                            "$element1 hashcode is equal to $element2 hashcode"
                        }
                    }
                }
            }
        }
    }
}
