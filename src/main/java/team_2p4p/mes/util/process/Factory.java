package team_2p4p.mes.util.process;

import lombok.Getter;

@Getter
public class Factory {
    private Measurement measurement;
    private PreProcessing preProcessing;
    private LiquidSystem liquidSystem;
    private FillPouchProcessing fillPouchProcessing;
    private FillStickProcessing fillStickProcessing;
    private CheckProcessing checkProcessing;
    private Packing packing;

    private static Factory factory;

    private Factory(){
        measurement = new Measurement();
        preProcessing = new PreProcessing();
        liquidSystem = new LiquidSystem();
        fillPouchProcessing = new FillPouchProcessing();
        fillStickProcessing = new FillStickProcessing();
        checkProcessing = new CheckProcessing();
        packing = new Packing();
    }

    public static Factory getInstance() {
        // 주의 - 여러개의 스레드가 동시에 if문을 통과하는 경우 여러개의 인스턴스 생성 가능
        if (factory == null) {
            factory = new Factory();
        }

        return factory;
    }

    public void clearList(){

        getMeasurement().getConfirmList().clear();
        getPreProcessing().getConfirmList().clear();
        getLiquidSystem().getConfirmList().clear();
        getFillPouchProcessing().getConfirmList().clear();
        getFillStickProcessing().getConfirmList().clear();
        getCheckProcessing().getConfirmList().clear();
        getPacking().getConfirmList().clear();

    }
}
