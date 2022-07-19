package com.sample.temp.cashblock_roulette_flutter1

import android.util.Log
import com.avatye.cashblock.CashBlockSDK
import com.avatye.cashblock.base.component.entity.user.Profile
import com.avatye.cashblock.business.model.specify.GenderType
import com.avatye.cashblock.feature.roulette.CashBlockRoulette
import com.avatye.cashblock.feature.roulette.component.model.listener.ITicketCount
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

/**
 * 캐시블록의 “룰렛”을 실행하기 위해서는 “초기화 및 기본설정”이 완료 되어야 합니다.

 * SDK 초기화 → 세션연동 → 유저 정보 연동 → 룰렛 실행
 * application -> CashBlockSDK.initialize()
 * Main activity -> CashBlockSDK.sessionStart(), CashBlockSDK.sessionEnd()
 * profile -> CashBlockSDK.setUserProfile()
 * launch
 */
class MainActivity : FlutterActivity() {
    companion object {
        val METHOD_CHANNEL = "avatye.cashblock.roulette/sample"
    }


    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, METHOD_CHANNEL)
            .setMethodCallHandler { call, result ->
                when (call.method) {
                    "cashBlock_init" -> {
                        val userId: String = call.arguments.toString()
                        setUserProfile(userId = userId)
                    }
                    "cashBlock_start" -> {
                        launchRoulette()
                    }
                    "cashBlock_roulette_ticket_condition" -> {
                        result.success(getTicketCondition())
                    }
                }
            }
    }


    /** 캐시블록 - 세션 시작 */
    private fun startBlockSession() {
        Log.e("Core@Block", "MainActivity -> startBlockSession")
        CashBlockSDK.sessionStart(context = this)
    }


    /** 사용자 프로필을 등록 (필수) */
    private fun setUserProfile(userId: String) {
        CashBlockSDK.setUserProfile(
            context = this,
            profile = Profile(
                userId = userId,
                birthYear = 2000,
                gender = GenderType.MALE
            )
        )

        Log.e("Core@Block", "MainActivity -> setUserProfile -> { getUserProifle: ${CashBlockSDK.getUserProfile(context = this)} }")
    }


    /** 캐시블록 - 룰렛 시작  */
    private fun launchRoulette() {
        Log.e("Core@Block", "MainActivity -> launchRoulette")
        CashBlockRoulette.launch(context = this)
    }


    /**
     * 캐시블록 - 룰렛 티켓 조회
     * balance : 보유수량
     * condition: 받을 수 있는 개수
     * */
    private fun getTicketCondition(): List<Int> {
        var ticketCondition = listOf<Int>()
        CashBlockRoulette.checkTicketCondition(context = this, listener = object : ITicketCount {
            override fun callback(balance: Int, condition: Int) {
                Log.e("Core@Block", "MainActivity -> ticketCondition -> { balance: $balance, condition: $condition }")
                ticketCondition = listOf(balance, condition)
            }
        })

        return ticketCondition
    }


    /**
     * 캐시블록 - 세션 종료
     * 메인 액티비티의 'onDestroy()' 함수에 적용
     */
    override fun onDestroy() {
        Log.e("Core@Block", "MainActivity -> onDestroy")
        CashBlockSDK.sessionEnd(context = this)
        super.onDestroy()
    }
}