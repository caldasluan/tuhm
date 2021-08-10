package br.com.pignata.tuhm.enum

enum class HeuristicEnum(val id: Int, val begin: Int, val end: Int) {
    HU1(1, 0, 22),
    HU2(2, 22, 32),
    HU3(3, 32, 52),
    HU4(4, 52, 69),
    HU5(5, 69, 82),
    HU6(6, 82, 104),
    HU7(7, 104, 110),
    HU8(8, 110, 119),
    HU9(9, 119, 132),
    HU10(10, 132, 150),
    HU11(11, 150, 164),
    HU12(12, 164, 176),
    HU13(13, 176, 183);

    companion object {
        fun getHeuristicWithId(id: Int): HeuristicEnum = when (id) {
            0 -> HU1
            1 -> HU2
            2 -> HU3
            3 -> HU4
            4 -> HU5
            5 -> HU6
            6 -> HU7
            7 -> HU8
            8 -> HU9
            9 -> HU10
            10 -> HU11
            11 -> HU12
            12 -> HU13
            else -> HU1
        }

        fun getHeuriscWithSubHeuristic(id: Int): HeuristicEnum {
            HeuristicEnum.values().forEach {
                if (id < it.end) return it
            }
            return HU1
        }
    }
}