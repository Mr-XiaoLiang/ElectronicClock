package liang.lollipop.electronicclock.utils

import java.lang.StringBuilder
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.abs

/**
 * @author lollipop
 * @date 2019-09-27 23:49
 * 一个农历计算器
 * @param year 年份
 * @param month 月份 [0~11]
 */
class LunarCalendar private constructor(private val year: Int, private val month: Int) {

    companion object {
        /**
         * 农历信息
         */
        private val lunarInfo = intArrayOf(
            0x4bd8, 0x4ae0, 0xa570, 0x54d5, 0xd260, 0xd950, 0x5554, 0x56af, 0x9ad0, 0x55d2,
            0x4ae0, 0xa5b6, 0xa4d0, 0xd250, 0xd255, 0xb54f, 0xd6a0, 0xada2, 0x95b0, 0x4977,
            0x497f, 0xa4b0, 0xb4b5, 0x6a50, 0x6d40, 0xab54, 0x2b6f, 0x9570, 0x52f2, 0x4970,
            0x6566, 0xd4a0, 0xea50, 0x6a95, 0x5adf, 0x2b60, 0x86e3, 0x92ef, 0xc8d7, 0xc95f,
            0xd4a0, 0xd8a6, 0xb55f, 0x56a0, 0xa5b4, 0x25df, 0x92d0, 0xd2b2, 0xa950, 0xb557,
            0x6ca0, 0xb550, 0x5355, 0x4daf, 0xa5b0, 0x4573, 0x52bf, 0xa9a8, 0xe950, 0x6aa0,
            0xaea6, 0xab50, 0x4b60, 0xaae4, 0xa570, 0x5260, 0xf263, 0xd950, 0x5b57, 0x56a0,
            0x96d0, 0x4dd5, 0x4ad0, 0xa4d0, 0xd4d4, 0xd250, 0xd558, 0xb540, 0xb6a0, 0x95a6,
            0x95bf, 0x49b0, 0xa974, 0xa4b0, 0xb27a, 0x6a50, 0x6d40, 0xaf46, 0xab60, 0x9570,
            0x4af5, 0x4970, 0x64b0, 0x74a3, 0xea50, 0x6b58, 0x5ac0, 0xab60, 0x96d5, 0x92e0,
            0xc960, 0xd954, 0xd4a0, 0xda50, 0x7552, 0x56a0, 0xabb7, 0x25d0, 0x92d0, 0xcab5,
            0xa950, 0xb4a0, 0xbaa4, 0xad50, 0x55d9, 0x4ba0, 0xa5b0, 0x5176, 0x52bf, 0xa930,
            0x7954, 0x6aa0, 0xad50, 0x5b52, 0x4b60, 0xa6e6, 0xa4e0, 0xd260, 0xea65, 0xd530,
            0x5aa0, 0x76a3, 0x96d0, 0x4afb, 0x4ad0, 0xa4d0, 0xd0b6, 0xd25f, 0xd520, 0xdd45,
            0xb5a0, 0x56d0, 0x55b2, 0x49b0, 0xa577, 0xa4b0, 0xaa50, 0xb255, 0x6d2f, 0xada0,
            0x4b63, 0x937f, 0x49f8, 0x4970, 0x64b0, 0x68a6, 0xea5f, 0x6b20, 0xa6c4, 0xaaef,
            0x92e0, 0xd2e3, 0xc960, 0xd557, 0xd4a0, 0xda50, 0x5d55, 0x56a0, 0xa6d0, 0x55d4,
            0x52d0, 0xa9b8, 0xa950, 0xb4a0, 0xb6a6, 0xad50, 0x55a0, 0xaba4, 0xa5b0, 0x52b0,
            0xb273, 0x6930, 0x7337, 0x6aa0, 0xad50, 0x4b55, 0x4b6f, 0xa570, 0x54e4, 0xd260,
            0xe968, 0xd520, 0xdaa0, 0x6aa6, 0x56df, 0x4ae0, 0xa9d4, 0xa4d0, 0xd150, 0xf252,
            0xd520)

        /**
         * 天干
         */
        private val Gan = arrayOf("甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸")
        /**
         * 地支
         */
        private val Zhi = arrayOf("子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥")
        /**
         * 生肖
         */
        private val Animals = arrayOf("鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪")
        /**
         * 节气
         */
        private val solarTerm = arrayOf("小寒", "大寒", "立春", "雨水", "惊蛰", "春分", "清明", "谷雨", "立夏",
            "小满", "芒种", "夏至", "小暑", "大暑", "立秋", "处暑", "白露", "秋分", "寒露", "霜降", "立冬",
            "小雪", "大雪", "冬至")

        private fun getSolarTerm(index: Int): String {
            if (index >= 0 && index < solarTerm.size) {
                return solarTerm[index]
            }
            return ""
        }

        /**
         * 节气对应的计算值
         */
        private val sTermInfo = intArrayOf(0, 21208, 42467, 63836, 85337, 107014, 128867, 150921, 173149,
            195551, 218072, 240693, 263343, 285989, 308563, 331033, 353350, 375494, 397447, 419210,
            440795, 462224, 483532, 504758)
        /**
         * 星期", "个位
         */
        private val nStr1 = arrayOf("日", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十")
        /**
         * 十位
         * 分别代表十位的：0，1，2，3，4，5，6，7，8，9
         * 即：0，10，20，30，40，50，60，70，80，90
         */
        private val nStr2 = arrayOf("初", "十", "廿", "卅", "卌", "圩", "圆", "进", "枯", "枠")
        /**
         * 公历每一个月的天数
         */
        private val solarMonth = arrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        /**
         * 农历的月份
         */
        private val monthChinese = arrayOf("正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "冬", "腊")
        /**
         * 通胜十二建
         */
        private val jcName = arrayOf(
            arrayOf("建", "除", "满", "平", "定", "执", "破", "危", "成", "收", "开", "闭"),
            arrayOf("闭", "建", "除", "满", "平", "定", "执", "破", "危", "成", "收", "开"),
            arrayOf("开", "闭", "建", "除", "满", "平", "定", "执", "破", "危", "成", "收"),
            arrayOf("收", "开", "闭", "建", "除", "满", "平", "定", "执", "破", "危", "成"),
            arrayOf("成", "收", "开", "闭", "建", "除", "满", "平", "定", "执", "破", "危"),
            arrayOf("危", "成", "收", "开", "闭", "建", "除", "满", "平", "定", "执", "破"),
            arrayOf("破", "危", "成", "收", "开", "闭", "建", "除", "满", "平", "定", "执"),
            arrayOf("执", "破", "危", "成", "收", "开", "闭", "建", "除", "满", "平", "定"),
            arrayOf("定", "执", "破", "危", "成", "收", "开", "闭", "建", "除", "满", "平"),
            arrayOf("平", "定", "执", "破", "危", "成", "收", "开", "闭", "建", "除", "满"),
            arrayOf("满", "平", "定", "执", "破", "危", "成", "收", "开", "闭", "建", "除"),
            arrayOf("除", "满", "平", "定", "执", "破", "危", "成", "收", "开", "闭", "建")
        )

        /**
         * 通胜十二建的黄历描述
         */
        private val auspiciousDayArray = arrayOf(
            // 大吉
            AuspiciousDay("成", "成功、天帝纪万物成就的大吉日子，凡事皆顺",
                arrayOf("结婚", "开市", "修造", "动土", "安床", "破土", "安葬", "搬迁", "交易",
                    "求财", "出行", "立契", "竖柱", "裁种", "牧养"),
                arrayOf("诉讼"), 2),
            AuspiciousDay("收", "收成、收获，天帝宝库收纳的日子",
                arrayOf("祈福", "求嗣", "赴任", "嫁娶", "安床", "修造", "动土", "求学", "开市",
                    "交易", "买卖", "立契"),
                arrayOf("放债", "新船下水", "新车下地", "破土", "安葬"), 2),
            AuspiciousDay("开", "开始、开展的日子",
                arrayOf("祭祀", "祈福", "入学", "上任", "修造", "动土", "开市", "安床", "交易",
                    "出行", "竖柱"),
                arrayOf("放债", "诉讼", "安葬"), 2),
            // 次吉
            AuspiciousDay("建", "万物生育、强健、健壮的日子",
                arrayOf("赴任", "祈福", "求嗣", "破土", "安葬", "修造", "上梁", "求财", "置业",
                    "入学", "考试", "结婚", "动土", "签约", "交涉", "出行"),
                arrayOf("动土", "开仓", "掘井", "乘船", "新船下水", "新车下地", "维修器具"), 1),
            AuspiciousDay("除", "扫除恶煞、去旧迎新的日子",
                arrayOf("祭祀", "祈福", "婚姻", "出行", "入伙", "搬迁", "出货", "动土", "求医",
                    "交易"),
                arrayOf("结婚", "赴任", "远行", "签约"), 1),
            AuspiciousDay("满", "丰收、美满、天帝宝库积满的日子",
                arrayOf("嫁娶", "祈福", "移徙", "开市", "交易", "求财", "立契", "祭祀", "出行",
                    "牧养"),
                arrayOf("造葬", "赴任", "求医"), 1),
            // 平
            AuspiciousDay("平", "平常、官人集合平分的日子",
                arrayOf("嫁娶", "修造", "破土", "安葬", "牧养", "开市", "安床", "动土", "求嗣"),
                arrayOf("祈福", "求嗣", "赴任", "嫁娶", "开市", "安葬"), 0),
            AuspiciousDay("定", "安定、平常、天帝众客定座的日子",
                arrayOf("祭祀", "祈福", "嫁娶", "造屋", "装修", "修路", "开市", "入学", "上任", "入伙"),
                arrayOf("诉讼", "出行", "交涉"), 0),
            // 凶
            AuspiciousDay("执", "破日之从神，曰小耗，天帝执行万物赐天福，较差的日子",
                arrayOf("造屋", "装修", "嫁娶", "收购", "立契", "祭祀"),
                arrayOf("开市", "求财", "出行", "搬迁"), -1),
            AuspiciousDay("破", "日月相冲，曰大耗，斗柄相冲相向必破坏的日子，大事不宜",
                arrayOf("破土", "拆卸", "求医"),
                arrayOf("嫁娶", "签约", "交涉", "出行", "搬迁"), -1),
            AuspiciousDay("危", "危机、危险，诸事不宜的日子",
                arrayOf("祭祀", "祈福", "安床", "拆卸", "破土"),
                arrayOf("登山", "乘船", "出行", "嫁娶", "造葬", "迁徙"), -1),
            AuspiciousDay("闭", "关闭、收藏、天地阴阳闭寒的日子",
                arrayOf("祭祀", "祈福", "筑堤", "埋池", "埋穴", "造葬", "填补", "修屋"),
                arrayOf("开市", "出行", "求医", "手术", "嫁娶"), -1)
        )

        private val unknownAuspiciousDay = AuspiciousDay("虚", "万物皆虚，万事皆允",
            arrayOf(), arrayOf(), 0)

        /**
         * 星宿信息
         */
        private val cnStar = arrayOf(
            arrayOf("室", "奎", "胄", "毕", "参", "鬼", "张", "角", "氐", "心", "斗", "虚"),
            arrayOf("壁", "娄", "昂", "觜", "井", "柳", "翼", "亢", "房", "尾", "女", "危"),
            arrayOf("奎", "胄", "毕", "参", "鬼", "星", "轸", "氐", "心", "箕", "虚", "室"),
            arrayOf("娄", "昂", "觜", "井", "柳", "张", "角", "房", "尾", "斗", "危", "壁"),
            arrayOf("胄", "毕", "参", "鬼", "星", "翼", "亢", "心", "箕", "女", "室", "奎"),
            arrayOf("昂", "觜", "井", "柳", "张", "轸", "氐", "尾", "斗", "虚", "壁", "娄"),
            arrayOf("毕", "参", "鬼", "星", "翼", "角", "房", "箕", "女", "危", "奎", "胄"),
            arrayOf("觜", "井", "柳", "张", "轸", "亢", "心", "斗", "虚", "室", "娄", "昂"),
            arrayOf("参", "鬼", "星", "翼", "角", "氐", "尾", "女", "危", "壁", "胄", "毕"),
            arrayOf("井", "柳", "张", "轸", "亢", "房", "箕", "虚", "室", "奎", "昂", "觜"),
            arrayOf("鬼", "星", "翼", "角", "氐", "心", "斗", "危", "壁", "娄", "毕", "参"),
            arrayOf("柳", "张", "轸", "亢", "房", "尾", "女", "室", "奎", "胄", "觜", "井"),
            arrayOf("星", "翼", "角", "氐", "心", "箕", "虚", "壁", "娄", "昂", "参", "鬼"),
            arrayOf("张", "轸", "亢", "房", "尾", "斗", "危", "奎", "胄", "毕", "井", "柳"),
            arrayOf("翼", "角", "氐", "心", "箕", "女", "室", "娄", "昂", "觜", "鬼", "星"),
            arrayOf("轸", "亢", "房", "尾", "斗", "虚", "壁", "胄", "毕", "参", "柳", "张"),
            arrayOf("角", "氐", "心", "箕", "女", "危", "奎", "昂", "觜", "井", "星", "翼"),
            arrayOf("亢", "房", "尾", "斗", "虚", "室", "娄", "毕", "参", "鬼", "张", "轸"),
            arrayOf("氐", "心", "箕", "女", "危", "壁", "胄", "觜", "井", "柳", "翼", "角"),
            arrayOf("房", "尾", "斗", "虚", "室", "奎", "昂", "参", "鬼", "星", "轸", "亢"),
            arrayOf("心", "箕", "女", "危", "壁", "娄", "毕", "井", "柳", "张", "角", "氐"),
            arrayOf("尾", "斗", "虚", "室", "奎", "胄", "觜", "鬼", "星", "翼", "亢", "房"),
            arrayOf("箕", "女", "危", "壁", "娄", "昂", "参", "柳", "张", "轸", "氐", "心"),
            arrayOf("斗", "虚", "室", "奎", "胄", "毕", "井", "星", "翼", "角", "房", "尾"),
            arrayOf("女", "危", "壁", "娄", "昂", "觜", "鬼", "张", "轸", "亢", "心", "箕"),
            arrayOf("虚", "室", "奎", "胄", "毕", "参", "柳", "翼", "角", "氐", "尾", "斗"),
            arrayOf("危", "壁", "娄", "昂", "觜", "井", "星", "轸", "亢", "房", "箕", "女"),
            arrayOf("室", "奎", "胄", "毕", "参", "鬼", "张", "角", "氐", "心", "斗", "虚"),
            arrayOf("壁", "娄", "昂", "觜", "井", "柳", "翼", "亢", "房", "尾", "女", "危"),
            arrayOf("奎", "胄", "毕", "参", "鬼", "星", "轸", "氐", "心", "箕", "虚", "室")
        )

        /**
         * 星宿的描述信息
         */
        private val cnStarInfo = arrayOf(
            // 东方苍龙
            ChineseStar("角", "东方苍龙", "属木，为蛟",
                "为东方七宿之首，有两颗星如苍龙的两角。龙角，乃斗杀之首冲，故多凶",
                arrayOf("角宿值日不非轻", "祭祀婚姻事不成", "埋葬若还逢此日", "三年之内有灾惊")),
            ChineseStar("亢", "东方苍龙", "属金，为龙",
                "是东方第二宿，为苍龙的颈。龙颈，有龙角之护卫，变者带动全身，故多吉",
                arrayOf("亢宿之星事可求", "婚姻祭祀有来头", "葬埋必出有官贵", "开门放水出公侯")),
            ChineseStar("氐", "东方苍龙", "属土，为貉",
                "氐，为根为本，如木之有根始能往上支天柱、往下扎深根，但当其根露现时即是冬寒草木枯黄之时。" +
                        "《史记》记载：“氐，东方之宿，氐者言万物皆至也。”氐宿是东方第三宿，为苍龙之胸，" +
                        "万事万物皆了然于心。龙胸，乃龙之中心要害，重中之重，故多吉",
                arrayOf("氐宿之星吉庆多", "招得横财贺有功", "葬埋若还逢此日", "一年之内进钱财")),
            ChineseStar("房", "东方苍龙", "为日，为兔",
                "为东方第四宿，为苍龙腹房，古人也称之为“天驷”，取龙为天马和房宿有四颗星之意。" +
                        "龙腹，五脏之所在，万物在这里被消化，故多凶",
                arrayOf("房宿值日事难成", "办事多半不吉庆", "葬埋多有不吉利", "起造三年有灾殃")),
            ChineseStar("心", "东方苍龙", "为月，为狐",
                "为东方第五宿，为苍龙腰部。心为火，是夏季第一个月应候的星宿，常和房宿连用，" +
                        "用来论述“中央支配四方”。龙腰，肾脏之所在，新陈代谢的源泉，不可等闲视之，故多凶",
                arrayOf("心宿恶星元非横", "起造男女事有伤", "坟葬不可用此日", "三年之内见瘟亡")),
            ChineseStar("尾", "东方苍龙", "属火，为虎",
                "为东方第六宿，尾宿九颗星形成苍龙之尾。龙尾，是斗杀中最易受到攻击部位，故多凶",
                arrayOf("尾宿之日不可求", "一切兴工有犯仇", "若是婚姻用此日", "三年之内有悲哀")),
            ChineseStar("箕", "东方苍龙", "属水，为豹",
                "为东方最后一宿，为龙尾摆动所引发之旋风。故箕宿好风，一旦特别明亮就是起风的预兆，" +
                        "因此又代表好调弄是非的人物、主口舌之象，故多凶",
                arrayOf("箕宿值日害男女", "官非口舌入门来", "一切修造不用利", "婚姻孤独守空房")),

            // 北方玄武
            ChineseStar("斗", "北方玄武", "属水，为獬",
                "为北方之首宿，因其星群组合状如斗而得名，古人又称“天庙”，是属于天子的星。" +
                        "天子之星常人是不可轻易冒犯的，故多凶",
                arrayOf("斗宿值日不吉良", "婚姻祭祀不吉昌", "葬埋不可用此日", "百般万事有灾殃")),
            ChineseStar("牛", "北方玄武", "属金，为牛",
                "为北方第二宿，因其星群组合如牛角而得名，其中最著名的是织女与牵牛星，虽然牛郎与" +
                        "织女的忠贞爱情能让数代人倾心感动，然最终还是无法逃脱悲剧性的结局，故牛宿多凶",
                arrayOf("牛宿值日利不多", "一切修造事灾多", "葬埋修造用此日", "卖尽田庄不记丘")),
            ChineseStar("女", "北方玄武", "属土，为蝠",
                "为北方第三宿，其星群组合状如箕，亦似“女”字，古时妇女常用簸箕颠簸五谷，" +
                        "去弃糟粕留取精华，故女宿多吉",
                arrayOf("女宿值日吉庆多", "起造兴工事事昌", "葬埋婚姻用此日", "三年之内进田庄")),
            ChineseStar("虚", "北方玄武", "为日，为鼠",
                "为北方第四宿，古人称为“天节”。当半夜时虚宿居于南中正是冬至的节令。" +
                        "冬至一阳初生，为新的一年即将开始，如同子时一阳初生意味着新的一天开始一样，" +
                        "给人以美好的期待和希望，故虚宿多吉",
                arrayOf("虚宿值日吉庆多", "祭祀婚姻大吉昌", "埋葬若还逢此日", "一年之内进钱财")),
            ChineseStar("危", "北方玄武", "为月，为燕",
                "为北方第五宿，居龟蛇尾部之处，故此而得名“危”（战斗中，断后者常常有危险）。" +
                        "危者，高也，高而有险，故危宿多凶",
                arrayOf("危宿值日不多吉", "灾祸必定注瘟亡", "一切修营尽不利", "灾多吉少事成灾")),
            ChineseStar("室", "北方玄武", "属火，为猪",
                "为北方第六宿，因其星群组合象房屋状而得名“室”（象一所覆盖龟蛇之上的房子），" +
                        "房屋乃居住之所，人之所需，故室宿多吉",
                arrayOf("室宿值日大吉利", "婚姻祭祀主恩荣", "葬埋苦还逢此日", "三年必定进田庄")),
            ChineseStar("壁", "北方玄武", "属水，为貐",
                "为北方第七宿，居室宿之外，形如室宿的围墙，故此而得名“壁”。墙壁，乃家园之屏障，故壁宿多吉",
                arrayOf("壁宿之星好利宜", "祭祀兴工吉庆多", "修造安门逢此日", "三朝七日进钱财")),

            // 西方白虎
            ChineseStar("奎", "西方白虎", "属木，为狼",
                "为西方第一宿，有天之府库的意思，故奎宿多吉",
                arrayOf("奎宿值日好安营", "一切修造大吉昌", "葬埋婚姻用此日", "朝朝日日进田庄")),
            ChineseStar("娄", "西方白虎", "属金，为狗",
                "为西方第二宿，娄，同“屡”，有聚众的含意，也有牧养众畜以供祭祀的意思，故娄宿多吉",
                arrayOf("娄宿之星吉庆多", "婚姻祭祀主荣华", "开门放水用此日", "三年之内主官班")),
            ChineseStar("胃", "西方白虎", "属土，为雉",
                "为西方第三宿，如同人体胃之作用一样，胃宿就象天的仓库屯积粮食，故胃宿多吉",
                arrayOf("胃宿修造事亨通", "祭祀婚姻贺有功", "葬埋若还逢此日", "田园五谷大登丰")),
            ChineseStar("昴", "西方白虎", "为日，为鸡",
                "为西方第四宿，居白虎七宿的中央，在古文中西从卯，西为秋门，一切已收获入内，" +
                        "该是关门闭户的时候了，故昴宿多凶",
                arrayOf("昴宿值日有灾殃", "凶多吉少不寻常", "一切兴工多不利", "朝朝日日有瘟伤")),
            ChineseStar("毕", "西方白虎", "为月，为鸟",
                "为西方第五宿，又名“罕车”，相当于边境的军队，又“毕”有“完全”之意，故毕宿多吉",
                arrayOf("毕宿造作主兴隆", "祭祀开门吉庆多", "一切修造主大旺", "钱财牛马满山川")),
            ChineseStar("觜", "西方白虎", "属火，为猴",
                "为西方第六宿，居白虎之口，口福之象征，故觜宿多吉",
                arrayOf("觜宿值日主吉良", "埋葬修造主荣昌", "若是婚姻用此日", "三年之内降麒麟")),
            ChineseStar("参", "西方白虎", "属水，为猿",
                "为西方第七宿，居白虎之前胸，虽居七宿之末但为最要害部位，故参宿多吉",
                arrayOf("参宿造作事兴隆", "富贵荣华胜石崇", "葬埋婚姻多吉庆", "衣粮牛马满家中")),

            // 南方朱雀
            ChineseStar("井", "南方朱雀", "属水，为犴",
                "为南方第一宿，其组合星群状如网，由此而得名“井”（井字如网状）。井宿就象一张迎头之网，" +
                        "又如一片无底汪洋（请参阅神话传说中的“精卫填海”故事），故井宿多凶",
                arrayOf("井宿值日事无通", "凶多吉少有瘟灾", "一切所求皆不利", "钱财耗散百灾非")),
            ChineseStar("鬼", "南方朱雀", "属金，为羊",
                "为南方第二宿，犹如一顶戴在朱雀头上的帽子，鸟类在受到惊吓时头顶羽毛成冠状，" +
                        "人们把最害怕而又并不存在的东西称作“鬼”，鬼宿因此而得名，主惊吓，故多凶",
                arrayOf("鬼宿值日不非轻", "一切所求事有惊", "买卖求财都不利", "家门灾祸散零丁")),
            ChineseStar("柳", "南方朱雀", "属土，为獐",
                "为南方第三宿，居朱雀之嘴，其状如柳叶（鸟类嘴之形状大多如此），嘴为进食之用，故柳宿多吉",
                arrayOf("柳宿修造主钱财", "富贵双全入家来", "葬埋婚姻用此日", "多招福禄主荣昌")),
            ChineseStar("星", "南方朱雀", "为日，为马",
                "为南方第四宿，居朱雀之目，鸟类的眼睛多如星星般明亮，故由此而得名“星”。" +
                        "俗话说“眼里不揉沙子”，故星宿多凶",
                arrayOf("星宿值日有悲哀", "凶多吉少有横灾", "一切兴工都不利", "家门灾祸起重重")),
            ChineseStar("张", "南方朱雀", "为月，为鹿",
            "为南方第五宿，居朱雀身体与翅膀连接处，翅膀张开才意味着飞翔，民间常有“开张大吉”等说法，故张宿多吉",
            arrayOf("张宿之星大吉昌", "祭祀婚姻日久长", "葬埋兴工用此日", "三年官禄进朝堂")),
            ChineseStar("翼", "南方朱雀", "属火，为蛇",
                "为南方第六宿，居朱雀之翅膀之位，故而得名“翼”，鸟有了翅膀才能腾飞，翼宿多吉",
                arrayOf("翼宿值日主吉祥", "年年进禄入门堂", "一切兴工有利益", "子孙富贵置田庄")),
            ChineseStar("轸", "南方朱雀", "属水，为蚓",
                "为南方第七宿，居朱雀之尾，鸟儿的尾巴是用来掌握方向的。古代称车箱底部后面的横木为“轸”，" +
                        "其部位与轸宿居朱雀之位相当，故此而得名。轸宿古称“天车”，“轸”有悲痛之意，故轸宿多凶",
                arrayOf("轸宿凶星不敢当", "人离财散有消亡", "葬埋婚姻皆不利", "朝朝日日有惊慌"))
        )

        private val unknownChineseStar = ChineseStar("无", "方位不明", "属相不明",
            "找不到对应的天象",
            arrayOf())

        //公历节日
        private val sFtv = arrayOf(
            Festival(1,1,"元旦", true),
            Festival(1,6,"中国13亿人口日"),
            Festival(1,10,"中国110宣传日"),

            Festival(2,2,"世界湿地日"),
            Festival(2,4,"世界抗癌症日"),
            Festival(2,7,"国际声援南非日"),
            Festival(2,10,"世界气象日"),
            Festival(2,14,"情人节"),
            Festival(2,21,"国际母语日"),

            Festival(3,3,"全国爱耳日"),
            Festival(3,8,"妇女节"),
            Festival(3,12, arrayOf("植树节", "孙中山逝世纪念日")),
            Festival(3,15,"消费者权益保护日"),
            Festival(3,21,"世界森林日"),
            Festival(3,22,"世界水日"),
            Festival(3,23,"世界气象日"),
            Festival(3,24,"世界防治结核病日"),

            Festival(4,1,"愚人节"),
            Festival(4,7,"世界卫生日"),
            Festival(4,22,"世界地球日"),

            Festival(5,1,"国际劳动节", true),
            Festival(5,4,"中国青年节"),
            Festival(5,5,"全国碘缺乏病日"),
            Festival(5,8,"世界红十字日"),
            Festival(5,1,"国际护士节"),
            Festival(5,15,"国际家庭日"),
            Festival(5,17,"世界电信日"),
            Festival(5,18,"国际博物馆日"),
            Festival(5,19, arrayOf("中国汶川地震哀悼日", "全国助残日")),
            Festival(5,20,"全国学生营养日"),
            Festival(5,22,"国际生物多样性日"),
            Festival(5,23,"国际牛奶日"),
            Festival(5,31,"世界无烟日"),

            Festival(6,1,"国际儿童节"),
            Festival(6,5,"世界环境日"),
            Festival(6,6,"全国爱眼日"),
            Festival(6,17,"防治荒漠化和干旱日"),
            Festival(6,23,"国际奥林匹克日"),
            Festival(6,25,"全国土地日"),
            Festival(6,26,"国际反毒品日"),

            Festival(7,1, arrayOf("建党节", "香港回归纪念日")),
            Festival(7,7,"抗日战争纪念日"),
            Festival(7,11,"世界人口日"),

            Festival(8,1,"八一建军节"),
            Festival(8,15,"日本正式宣布无条件投降日"),

            Festival(9,8,"国际扫盲日"),
            Festival(9,9,"毛泽东逝世纪念日"),
            Festival(9,10,"教师节"),
            Festival(9,16,"国际臭氧层保护日"),
            Festival(9,17,"国际和平日"),
            Festival(9,18,"九·一八事变纪念日"),
            Festival(9,20,"国际爱牙日"),
            Festival(9,27,"世界旅游日"),
            Festival(9,28,"孔子诞辰"),

            Festival(10,1, arrayOf("国庆节", "国际音乐节", "国际老人节"), true),
            Festival(10,2,"国际减轻自然灾害日"),
            Festival(10,4,"世界动物日"),
            Festival(10,8, arrayOf("世界视觉日", "全国高血压日")),
            Festival(10,9,"世界邮政日"),
            Festival(10,10, arrayOf("辛亥革命纪念日", "世界精神卫生日")),
            Festival(10,15,"国际盲人节"),
            Festival(10,16,"世界粮食节"),
            Festival(10,17,"世界消除贫困日"),
            Festival(10,22,"世界传统医药日"),
            Festival(10,24,"联合国日"),
            Festival(10,25,"人类天花绝迹日"),
            Festival(10,26,"足球诞生日"),
            Festival(10,31,"万圣节"),

            Festival(11,7,"十月社会主义革命纪念日"),
            Festival(11,8,"中国记者日"),
            Festival(11,9,"消防宣传日"),
            Festival(11,10,"世界青年节"),
            Festival(11,12,"孙中山诞辰"),
            Festival(11,14,"世界糖尿病日"),
            Festival(11,17,"国际大学生节"),

            Festival(12,1,"世界艾滋病日"),
            Festival(12,3,"世界残疾人日"),
            Festival(12,9,"世界足球日"),
            Festival(12,10,"世界人权日"),
            Festival(12,12,"西安事变纪念日"),
            Festival(12,13,"南京大屠杀"),
            Festival(12,20,"澳门回归纪念日"),
            Festival(12,21,"国际篮球日"),
            Festival(12,24,"平安夜"),
            Festival(12,25, arrayOf("圣诞节", "世界强化免疫日")),
            Festival(12,26,"毛泽东诞辰")
        )
        //农历节日
        private val lFtv = arrayOf(
            Festival(1,1,"春节",true),
            Festival(1,2,"大年初二",true),
            Festival(1,3,"大年初三",true),
            Festival(1,4,"大年初四",true),
            Festival(1,5,"大年初五",true),
            Festival(1,6,"大年初六",true),
            Festival(1,7,"大年初七",true),
            Festival(1,5,"路神生日"),
            Festival(1,15,"元宵节"),
            Festival(2,2,"龙抬头"),
            Festival(2,19,"观世音圣诞"),
            Festival(4,4,"寒食节"),
            Festival(4,8,"佛诞节 "),
            Festival(5,5,"端午节",true),
            Festival(6,6, arrayOf("天贶节", "姑姑节")),
            Festival(6,24,"彝族火把节"),
            Festival(7,7,"七夕节"),
            Festival(7,14,"鬼节(南方)"),
            Festival(7,15,"盂兰节"),
            Festival(7,30,"地藏节"),
            Festival(8,15,"中秋节",true),
            Festival(9,9,"重阳节"),
            Festival(10,1,"祭祖节"),
            Festival(11,17,"阿弥陀佛圣诞"),
            Festival(12,8, arrayOf("腊八节", "释迦如来成道日")),
            Festival(12,23,"过小年"),
            Festival(12,29,"腊月二十九",true),
            Festival(1,0,"除夕",true)
        )

        /**
         * 按星期计算的节日
         */
        private val internationalFestivalArray = arrayOf (
            InternationalFestival(1, 0, 0, "黑人节"),
            InternationalFestival(1, -1, 0, "世界麻风日"),
            InternationalFestival(1, 1, 1, "日本成人节"),
            InternationalFestival(5, 1, 0, "母亲节"),
            InternationalFestival(5, 2, 0, "全国助残日"),
            InternationalFestival(6, 2, 0, "父亲节"),
            InternationalFestival(7, 0, 6, "合作节"),
            InternationalFestival(7, 2, 0, "被奴役国家周"),
            InternationalFestival(9, 2, 2, "国际和平日"),
            InternationalFestival(9, 3, 0, "国际聋人节"),
            InternationalFestival(9, 3, 0, "世界儿童日"),
            InternationalFestival(10, 0, 1, "国际住房日"),
            InternationalFestival(11, 3, 4, "感恩节")
        )

        /**
         * 日历的计算类
         */
        private val staticCalendar: Calendar by lazy {
            Calendar.getInstance().apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        }

        /**
         * 缓存
         */
        private val cache = HashMap<String, LunarCalendar>()

        /**
         * 受到支持的最大的年份
         */
        val maxYear = 1900 + lunarInfo.size

        /**
         * 获取日期信息的方法
         * 获取某一个月的农历
         * @param month [0~11]
         */
        fun getCalendar(year: Int, month: Int): LunarCalendar {
            // 生成月历对应的key
            val key = "$year-$month"
            // 如果缓存中存在了，那么不做计算，直接返回
            val cacheCalendar = cache[key]
            if (cacheCalendar != null) {
                return cacheCalendar
            }
            // 否则的话，创建新的月历
            val newCalendar = LunarCalendar(year, month)
            newCalendar.calculation()
            cache[key] = newCalendar
            return newCalendar
        }

        private const val ONE_DAY = 1000L * 60 * 60 * 8

        /**
         * 获取月份信息
         */
        fun getMonth(timestamp: Long, run: (year: Int, month: Int) -> Unit) {
            // 更新时间戳
            staticCalendar.timeInMillis = timestamp + ONE_DAY
            // 得到对应的年月
            val year = staticCalendar.get(Calendar.YEAR)
            val month = staticCalendar.get(Calendar.MONTH)
            run(year, month)
        }

        fun timeInMillis(year: Int, month: Int, day: Int): Long {
            staticCalendar.set(year, month, day, 0, 0, 0)
            return staticCalendar.timeInMillis
        }

        /**
         * 获取日期信息的方法
         * 获取某一天的农历
         */
        fun getCalendar(timestamp: Long): Element {
            // 更新时间戳
            staticCalendar.timeInMillis = timestamp + ONE_DAY
            // 得到对应的年月
            val year = staticCalendar.get(Calendar.YEAR)
            val month = staticCalendar.get(Calendar.MONTH)
            val day = staticCalendar.get(Calendar.DAY_OF_MONTH)
            val lunarCalendar = getCalendar(year, month)
            return lunarCalendar.elementArray[day - 1]
        }

        /**
         * 返回农历 y年的总天数
         */
        private fun lYearDays(y: Int): Int {
            var i = 0x8000
            var sum = 348
            while (i > 0x8) {
                sum += if (lunarInfo[y - 1900] and i != 0) 1 else 0
                i = i shr 1
            }
            return sum + leapDays(y)
        }

        /**
         * 返回农历 y年闰月的天数
         * 农历月份只有30或者29天
         */
        private fun leapDays(y: Int): Int {
            return if (leapMonth(y) != 0) {
                if (lunarInfo[y - 1899] and 0xF == 0xF) 30 else 29
            } else {
                0
            }
        }

        /**
         * 返回农历 y年闰哪个月 1-12 , 没闰返回 0
         */
        private fun leapMonth(y: Int): Int {
            val lm = lunarInfo[y - 1900] and 0xF
            return if (lm == 0xF) 0 else lm
        }

        /**
         * 返回农历 y年m月的总天数
         */
        private fun monthDays(y: Int, m: Int): Int {
            return if (lunarInfo[y - 1900] and (0x10000 shr m) != 0) 30 else 29
        }

        /**
         * 农历的日期
         */
        private fun dayChinese(day: Int): String {
            val ten = day / 10 % 10
            val single = day % 10
            if (single == 0) {
                if (ten == 1) {
                    return nStr2[0] + nStr1[10]
                } else if (ten > 1) {
                    return nStr1[ten] + nStr1[10]
                }
            }
            return nStr2[ten] + nStr1[single]
        }

        /**
         * 择日算法
         */
        private fun calConv2(yy: Int, mm: Int, dd: Int,
                             y: Int, d: Int, m: Int, dt: Int, nm: Int, nd: Int): String {
            val dy = d + dd
            return if (yy == 0 && dd == 6 || yy == 6 && dd == 0
                || yy == 1 && dd == 7 || yy == 7 && dd == 1
                || yy == 2 && dd == 8 || yy == 8 && dd == 2
                || yy == 3 && dd == 9 || yy == 9 && dd == 3
                || yy == 4 && dd == 10 || yy == 10 && dd == 4
                || yy == 5 && dd == 11 || yy == 11 && dd == 5) {
                "日值岁破 大事不宜"
            } else if (mm == 0 && dd == 6 || mm == 6 && dd == 0
                || mm == 1 && dd == 7 || mm == 7 && dd == 1
                || mm == 2 && dd == 8 || mm == 8 && dd == 2
                || mm == 3 && dd == 9 || mm == 9 && dd == 3
                || mm == 4 && dd == 10 || mm == 10 && dd == 4
                || mm == 5 && dd == 11 || mm == 11 && dd == 5) {
                "日值月破 大事不宜"
            } else if (y == 0 && dy == 911 || y == 1 && dy == 55
                || y == 2 && dy == 111 || y == 3 && dy == 75
                || y == 4 && dy == 311 || y == 5 && dy == 9
                || y == 6 && dy == 511 || y == 7 && dy == 15
                || y == 8 && dy == 711 || y == 9 && dy == 35) {
                "日值上朔 大事不宜"
            } else if (m == 1 && dt == 13 || m == 2 && dt == 11
                || m == 3 && dt == 9 || m == 4 && dt == 7
                || m == 5 && dt == 5 || m == 6 && dt == 3
                || m == 7 && dt == 1 || m == 7 && dt == 29
                || m == 8 && dt == 27 || m == 9 && dt == 25
                || m == 10 && dt == 23 || m == 11 && dt == 21
                || m == 12 && dt == 19) {
                "日值杨公十三忌 大事不宜"
            } else {
                ""
            }
        }

        /**
         * 根据关键字检索相应的描述
         */
        private fun getAuspiciousDayByKey(key: String): AuspiciousDay {
            for (day in auspiciousDayArray) {
                if (day.key == key) {
                    return day
                }
            }
            return unknownAuspiciousDay
        }

        /**
         * 返回公历 y年某m+1月的天数
         */
        private fun solarDays(y: Int, m: Int): Int {
            return if (m == 1) {
                if (y % 4 == 0 && y % 100 != 0 || y % 400 == 0) 29 else 28
            } else {
                solarMonth[m]
            }
        }

        /**
         * 传入 offset new Date 返回干支, 0=甲子
         */
        private fun ganZhi(num: Int): String {
            return Gan[num % 10] + Zhi[num % 12]
        }

        private fun ganZhi(num: Long): String {
            return ganZhi((num % 120).toInt())
        }

        /**
         * 某年的第n个节气为几日(从0小寒起算)
         */
        private fun sTerm(y: Int, n: Int): Int {
            val utcTime = -0x20237CBE720
            val time2 = BigDecimal(31556925974.7).multiply(BigDecimal(y - 1900)).add(
                BigDecimal(sTermInfo[n]).multiply(
                    BigDecimal.valueOf(60000L)
                )
            )
            val time = time2.add(BigDecimal.valueOf(utcTime))
            val offDate = Date(time.toLong())
            staticCalendar.time = offDate
            //日期从0算起
            return staticCalendar.get(Calendar.DATE)
        }

        /**
         * 返回阴历 (y年,m+1月)
         */
        private fun cyclical6(year: Int, month: Int): String {
            if (year >= 0 && month >= 0 && year < jcName.size) {
                val jc = jcName[year]
                if (month < jc.size) {
                    return jc[month]
                }
            }
            return ""
        }

        /**
         * 获取生肖
         */
        private fun getAnimals(year: Int): String {
            return Animals[(year - 1900) % Animals.size]
        }

        /**
         * 获取星宿的key
         */
        private fun getCnStar(lMonth: Int, lDay: Int): String {
            return cnStar[lDay - 1][lMonth - 1]
        }

        /**
         * 获取星宿信息的描述
         */
        private fun getCnStarInfoByKey(key: String): ChineseStar {
            for (star in cnStarInfo) {
                if (star.key == key) {
                    return star
                }
            }
            return unknownChineseStar
        }

        /**
         * 将文本竖起来的方法
         */
        fun stringToVertical(value: String, maxLines: Int): String {
            val valueLength = value.length
            if (valueLength < 2 || maxLines < 2) {
                return value
            }
            val builder = StringBuilder()
            var colCount: Int
            var lineCount: Int = maxLines
            if (valueLength <= maxLines) {
                colCount = 1
                lineCount = valueLength
            } else {
                colCount = valueLength / maxLines
                if (colCount * maxLines < valueLength) {
                    colCount++
                }
            }
            for (line in 0 until lineCount) {
                for (col in 0 until colCount) {
                    val index = col * maxLines + line
                    if (index >= valueLength) {
                        if (line < lineCount - 1) {
                            builder.append("\n")
                        }
                        break
                    }
                    builder.append(value[index])
                    if (col == colCount - 1 && line < lineCount - 1) {
                        builder.append("\n")
                    }
                }
            }
            return builder.toString()
        }

    }

    /**
     * 日历的计算类
     */
    private val calendar: Calendar
        get() {
            return staticCalendar
        }

    /**
     * 本月每一天的信息
     */
    val elementArray: Array<Element>
    /**
     * 本月的日期有几天
     */
    val length: Int
    /**
     * 本月第一天星期几
     */
    val firstWeek: Int

    var today: Element? = null
        private set

    init {
        // 设置为一个月的开始时间
        calendar.set(year, month, 1, 0, 0, 0)
        //公历当月1日星期几
        firstWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        // 设置当月的天数
        length = solarDays(year, month)
        // 根据天数设置本月的信息集合
        elementArray = Array(length) { Element() }
    }

    private fun calculation() {
        var lL = false
        var lD2: Long
        var lY = 0
        var lM = 0
        var lD = 1
        var lX = 0
        var tmp1: Int
        var tmp2: Int
        var lM2: Int?
        var lY2: Int? = null
        val cs1: Int? = null
        var cY: String //年柱
        var cM: String //月柱
        var cD: String //日柱
        val lDPOS = IntArray(3)
        var n = 0
        var firstLM = 0

        // 年柱 1900年立春后为庚子年(60进制36)
        cY = if (month < 2) {
            ganZhi(year - 1900 + 36 - 1)
        } else {
            ganZhi(year - 1900 + 36)
        }
        val spring = sTerm(year, 2) //立春日期

        // 月柱 1900年1月小寒以前为 丙子月(60进制12)
        val firstNode = sTerm(year, month * 2)//返回当月「节」为几日开始
        cM = ganZhi((year - 1900) * 12 + month + 12)

        lM2 = (year - 1900) * 12 + month + 12
        // 当月一日与 1900/1/1 相差天数
        // 1900/1/1与 1970/1/1 相差25567日, 1900/1/1 日柱为甲戌日(60进制10)
        calendar.set(year, month, 1, 0, 0, 0)
        val dayCyclical = calendar.timeInMillis / 86400000 + 25567 + 10
        for (i in 0 until this.length) {
            if (lD > lX) {
                calendar.set(year, month, i + 1, 0, 0, 0)
                val lDObj = Lunar(calendar.timeInMillis)     //农历
                lY = lDObj.year   //农历年
                lM = lDObj.month  //农历月
                lD = lDObj.day    //农历日
                lL = lDObj.isLeap //农历是否闰月
                lX = if (lL) leapDays(lY) else monthDays(lY, lM) //农历当月最后一天
                if (n == 0) {
                    firstLM = lM
                }
                lDPOS[n++] = i - lD + 1
            }

            // 依节气调整二月分的年柱, 以立春为界
            if (month == 1 && i + 1 == spring) {
                cY = ganZhi(year - 1900 + 36)
                lY2 = year - 1900 + 36
            }
            // 依节气月柱, 以「节」为界
            if (i + 1 == firstNode) {
                cM = ganZhi((year - 1900) * 12 + month + 13)
                lM2 = (year - 1900) * 12 + month + 13
            }
            lD2 = dayCyclical + i
            // 日柱
            cD = ganZhi(lD2)
            val element = elementArray[i]
            element.reset(year, month + 1, i + 1, nStr1[(i + this.firstWeek) % 7],
                lY, lM, lD++, lL, cY, cM, cD)
//            element.cDay = dayChinese(element.lDay)
            val paramterLy2 = if (lY2 == null) -1 else lY2 % 12
            val paramterLm2 = if (lM2 == null) -1 else lM2 % 12
            val paramterLd2 = lD2 % 12
            val paramterLy2b = if (lY2 == null) -1 else lY2 % 10
            val paramterLy2c = (lD2 % 10).toInt()
            val paramterLld = lD - 1
            element.sgz5 = calConv2(paramterLy2, paramterLm2, paramterLd2.toInt(),
                paramterLy2b, paramterLy2c, lM, paramterLld, month + 1, cs1 ?: -1)
            if (lM2 != null) {
                element.sgz3 = cyclical6(lM2 % 12, (lD2 % 12).toInt())
            }
        }

        //节气
        tmp1 = sTerm(year, month * 2) - 1
        tmp2 = sTerm(year, month * 2 + 1) - 1
        elementArray[tmp1].solarTerms = getSolarTerm(month * 2)
        elementArray[tmp2].solarTerms = getSolarTerm(month * 2 + 1)

        //国历节日
        for (sf in sFtv) {
            if (sf.month == month + 1) {
                val element = elementArray[sf.day - 1]
                element.solarFestival.addAll(sf.name)
                if (sf.isHoliday) {
                    element.holiday = true
                }
            }
        }

        //农历节日
        for (lf in lFtv) {
            tmp1 = lf.month - firstLM
            if (tmp1 == -11) {
                tmp1 = 1
            }
            if (tmp1 in 0 until n) {
                tmp2 = lDPOS[tmp1] + lf.day - 1
                if (tmp2 >= 0 && tmp2 < this.length) {
                    val element = elementArray[tmp2]
                    element.lunarFestival.addAll(lf.name)
                    if (lf.isHoliday) {
                        element.holiday = true
                    }
                }
            }
        }

        //复活节只出现在3或4月
        if (month == 2 || month == 3) {
            val estDay = Easter(year, calendar)
            if (month == estDay.month) {
                elementArray[estDay.day - 1].solarFestival.add("复活节(Easter Sunday)")
            }
        }


        //黑色星期五
        if ((this.firstWeek + 12) % 7 == 5) {
            elementArray[12].solarFestival += "黑色星期五"
        }

        //今日
        calendar.timeInMillis = System.currentTimeMillis() + ONE_DAY
        if (year == calendar.get(Calendar.YEAR) &&
                month == calendar.get(Calendar.MONTH)) {
            val index = calendar.get(Calendar.DAY_OF_MONTH) - 1
            elementArray[index].isToday = true
            today = elementArray[index]
        } else {
            today = null
        }

        //国际节日
        for (festival in internationalFestivalArray) {
            if (festival.month == month + 1) {
                var day = 0
                // 如果是倒数，那么倒着数
                if (festival.weekIndex < 0) {
                    day = elementArray.size
                }
                // 偏移到第几周
                day += festival.weekIndex * 7
                // 偏移到星期
                day += festival.weekDay - firstWeek
                if (day < 0) {
                    day += 7
                }
                val element = elementArray[day % elementArray.size]
                element.solarFestival.add(festival.name)
            }
        }
    }

    private class Easter constructor(y: Int, calendar: Calendar) {

        var month: Int = 0
        var day: Int = 0

        init {
            val term2 = sTerm(y, 5) //取得春分日期
            calendar.set(y, 2, term2, 0, 0, 0)
            val dayTerm2 = calendar.timeInMillis
            // df2.parse("$y-3-$term2 00:00:00")//取得春分的公历日期控件(春分一定出现在3月)
            val lDayTerm2 = Lunar(dayTerm2) //取得取得春分农历
            val lMlen = if (lDayTerm2.day < 15) {
                //取得下个月圆的相差天数
                15 - lDayTerm2.day
            } else {
                (if (lDayTerm2.isLeap) {
                    leapDays(y)
                } else{
                    monthDays(y, lDayTerm2.month)
                }) - lDayTerm2.day + 15
            }

            //一天等于 1000*60*60*24 = 86400000 毫秒
            calendar.timeInMillis = dayTerm2 + 86400000 * lMlen //求出第一次月圆为公历几日
            calendar.add(Calendar.DAY_OF_MONTH, 7 - calendar.get(Calendar.DAY_OF_WEEK))//求出下个周日

            this.month = calendar.get(Calendar.MONTH)
            this.day = calendar.get(Calendar.DAY_OF_MONTH)
        }
    }

    /**
     * 农历的数据计算
     */
    private class Lunar (now: Long) {
        /**
         * 是否是闰月
         */
        var isLeap: Boolean = false
            private set
        /**
         * 时间戳对应的年
         */
        val year: Int
        /**
         * 对应的月
         */
        val month: Int
        /**
         * 对应的日
         */
        val day: Int

        init {
            var temp = 0
            val time2 = -0x201b77f5c00L
            var offset = ((now - time2) / 86400000).toInt()
            var i = 1900
            while (i < 2100 && offset > 0) {
                temp = lYearDays(i)
                offset -= temp
                i++
            }

            if (offset < 0) {
                offset += temp
                i--
            }

            this.year = i
            val leap = leapMonth(i) //闰哪个月
            this.isLeap = false

            i = 1
            while (i < 13 && offset > 0) {
                //闰月
                if (leap > 0 && i == leap + 1 && !this.isLeap) {
                    --i
                    this.isLeap = true
                    temp = leapDays(this.year)
                } else {
                    temp = monthDays(this.year, i)
                }

                //解除闰月
                if (this.isLeap && i == leap + 1) {
                    this.isLeap = false
                }

                offset -= temp
                i++
            }

            if (offset == 0 && leap > 0 && i == leap + 1) {
                if (this.isLeap) {
                    this.isLeap = false
                } else {
                    this.isLeap = true
                    --i
                }
            }
            if (offset < 0) {
                offset += temp
                --i
            }
            this.month = i
            this.day = offset + 1
        }

    }

    class Element {
        /** 公元年份 **/
        var sYear: Int = 0
            private set
        /** 公元月份 [1~12] **/
        var sMonth: Int = 0
            private set
        /** 公元月内日期 **/
        var sDay: Int = 0
            private set
        /** 星期 中文 **/
        var week: String = ""
            private set
        /** 农历年份数字 **/
        var lYear: Int = 0
            private set
        /** 农历月份数字 **/
        var lMonth: Int = 0
            private set
        /** 农历月内日期数字 **/
        var lDay: Int = 0
            private set
        /** 农历月份中文 **/
        var lMonthChinese: String = ""
            private set
        /** 农历月内日期中文 **/
        var lDayChinese: String = ""
            private set
        /** 是否是农历闰月 **/
        var isLeap: Boolean = false
            private set
        /** 八字 年柱 **/
        var cYear: String = ""
            private set
        /** 八字 月柱 **/
        var cMonth: String = ""
            private set
        /** 八字日柱 **/
        var cDay: String = ""
        /** 今天 **/
        var isToday = false
        /** 农历节日 **/
        val lunarFestival = ArrayList<String>()
        /** 公元历节日 **/
        val solarFestival = ArrayList<String>()
        /** 节气 **/
        var solarTerms: String = ""

        var sgz5: String = ""

        /** 择日描述 **/
        var auspiciousDay = unknownAuspiciousDay
            private set
        /**
         * 通胜十二建
         */
        var sgz3: String = ""
            set(value) {
                field = value
                auspiciousDay = getAuspiciousDayByKey(value)
            }
        /** 假期 **/
        var holiday = false
        /** 生肖 **/
        var animals = ""
            private set

        /**
         * 星宿信息
         */
        var cnStar: ChineseStar = unknownChineseStar

        /**
         * 是否是吉日
         */
        val isAuspiciousDay: Boolean
            get() {
                return auspiciousDay.type > 1
            }

        fun reset(sYear: Int, sMonth: Int, sDay: Int, week: String,
                  lYear: Int, lMonth: Int, lDay: Int, isLeap: Boolean,
                  cYear: String, cMonth: String, cDay: String) {
            this.isToday = false
            //瓣句
            this.sYear = sYear
            this.sMonth = sMonth
            this.sDay = sDay
            this.week = week
            //农历
            this.lYear = lYear
            this.lMonth = lMonth
            this.lDay = lDay
            this.isLeap = isLeap
            //中文
            this.lMonthChinese = monthChinese[lMonth - 1]
            this.lDayChinese = dayChinese(lDay)
            //八字
            this.cYear = cYear
            this.cMonth = cMonth
            this.cDay = cDay
            // 节假日
            this.lunarFestival.clear()
            this.solarFestival.clear()
            this.solarTerms = ""
            // 生肖
            this.animals = getAnimals(sYear)
            // 星宿
            this.cnStar = getCnStarInfoByKey(getCnStar(lMonth, lDay))
        }

    }

    private class Festival(val month: Int, val day: Int,
                                val name: Array<String>, val isHoliday: Boolean = false) {
        constructor(month: Int, day: Int, name: String, isHoliday: Boolean = false): this(month, day, arrayOf(name), isHoliday)
    }

    /**
     * 择日数据类
     * @param key 日期的名称
     * @param detail 描述
     * @param matter 适合做的事情
     * @param taboo 不适合做的事情
     * @param type 日期类型，2：大吉，1：次吉，0：平，-1：凶
     */
    data class AuspiciousDay(val key: String, val detail: String, val matter: Array<String>,
                                     val taboo: Array<String>, val type: Int) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as AuspiciousDay

            if (key != other.key) return false
            if (detail != other.detail) return false
            if (!matter.contentEquals(other.matter)) return false
            if (!taboo.contentEquals(other.taboo)) return false
            if (type != other.type) return false

            return true
        }

        override fun hashCode(): Int {
            var result = key.hashCode()
            result = 31 * result + detail.hashCode()
            result = 31 * result + matter.contentHashCode()
            result = 31 * result + taboo.contentHashCode()
            result = 31 * result + type
            return result
        }
    }

    /**
     * 星宿的信息
     */
    data class ChineseStar(val key: String, val group: String,
                           val kind: String, val detail: String,
                           val inscription: Array<String>) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ChineseStar

            if (key != other.key) return false
            if (group != other.group) return false
            if (kind != other.kind) return false
            if (detail != other.detail) return false
            if (!inscription.contentEquals(other.inscription)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = key.hashCode()
            result = 31 * result + group.hashCode()
            result = 31 * result + kind.hashCode()
            result = 31 * result + detail.hashCode()
            result = 31 * result + inscription.contentHashCode()
            return result
        }
    }

    /**
     * 国际节日
     */
    data class InternationalFestival(val month: Int, val weekIndex: Int,
                                     val weekDay: Int, val name: String)

}