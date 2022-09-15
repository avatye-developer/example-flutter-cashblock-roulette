import androidx.annotation.NonNull;

import com.avatye.cashblock.CashBlockSDK;
import com.avatye.cashblock.base.component.entity.user.Profile;
import com.avatye.cashblock.business.model.specify.GenderType;
import com.avatye.cashblock.feature.roulette.CashBlockRoulette;
import com.avatye.cashblock.feature.roulette.component.model.listener.ITicketCount;

import java.util.ArrayList;
import java.util.List;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * 캐시블록의 “룰렛”을 실행하기 위해서는 “초기화 및 기본설정”이 완료 되어야 합니다.

 * SDK 초기화 → 유저 정보 연동 → 세션연동 → 룰렛 실행
 * 1. SDK 초기화       : CashBlockSDK.initialize()                               : Application
 * 2. 유저 정보 연동   : CashBlockSDK.setUserProfile()                           : MainActivity
 * 3. 세션연동        :  CashBlockSDK.sessionStart(), CashBlockSDK.sessionEnd()  : MainActivity
 * 4. 룰렛실행        :  CashBlockRoulette.launch                                : MainActivity
 */

public class MainActivity extends FlutterActivity {
    private static final String METHOD_CHANNEL = "avatye.cashblock.roulette/sample";

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), METHOD_CHANNEL)
                .setMethodCallHandler(new MethodChannel.MethodCallHandler() {
                    @Override
                    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
                        switch (call.method) {
                            case "cashBlock_init": {
                                final String userId = call.arguments.toString();
                                setUserProfile(userId);
                                startBlockSession();
                                break;
                            }
                            case "cashBlock_start": {
                                launchRoulette();
                                break;
                            }
                            case "cashBlock_roulette_ticket_condition": {
                                result.success(getTicketCondition());
                                break;
                            }
                        }
                    }
                });
    }

    /**
     * 유저 정보 연동 (필수)
     */
    private void setUserProfile(final String userId) {
        CashBlockSDK.setUserProfile(
                this,
                new Profile(userId, 2000, GenderType.MALE)
        );
    }


    /**
     * 세션 시작
     */
    private void startBlockSession() {
        CashBlockSDK.sessionStart(this);
    }


    /**
     * 룰렛 시작
     */
    private void launchRoulette() {
        CashBlockRoulette.launch(this);
    }


    /**
     * 룰렛 티켓 조회
     * balance : 보유수량
     * condition: 받을 수 있는 개수
     */
    private List<Integer> getTicketCondition() {
        final List<Integer> ticketCondition = new ArrayList<>();
        CashBlockRoulette.checkTicketCondition(this, new ITicketCount() {
            @Override
            public void callback(int balance, int condition) {
                ticketCondition.add(balance);
                ticketCondition.add(condition);
            }
        });
        return ticketCondition;
    }

    /**
     * 세션 종료
     * 메인 액티비티의 'onDestroy()' 함수에 적용
     */
    @Override
    protected void onDestroy() {
        CashBlockSDK.sessionEnd(this);
        super.onDestroy();
    }
}
