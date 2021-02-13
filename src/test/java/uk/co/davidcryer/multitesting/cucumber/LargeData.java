package uk.co.davidcryer.multitesting.cucumber;

import uk.co.davidcryer.multitesting.generated.tables.pojos.Large;
import uk.co.davidcryer.multitesting.large.LargeRequest;

import java.util.UUID;

public class LargeData {

    static LargeRequest request() {
        return LargeRequest.builder()
                .first("1")
                .second("2")
                .third("3")
                .fourth("4")
                .fifth("5")
                .sixth("6")
                .seventh("7")
                .eighth("8")
                .ninth("9")
                .tenth("10")
                .eleventh("11")
                .twelfth("12")
                .thirteenth("13")
                .fourteenth("14")
                .fifteenth("15")
                .sixteenth("16")
                .seventeenth("17")
                .eighteenth("18")
                .nineteenth("19")
                .twentieth("20")
                .build();
    }

    static Large entity() {
        return new Large(
                UUID.randomUUID().toString(),
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9",
                "10",
                "11",
                "12",
                "13",
                "14",
                "15",
                "16",
                "17",
                "18",
                "19",
                "20"
        );
    }
}
