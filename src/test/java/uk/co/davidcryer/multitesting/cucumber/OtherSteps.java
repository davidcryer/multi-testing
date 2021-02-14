package uk.co.davidcryer.multitesting.cucumber;

public class OtherSteps {

//    @Autowired
//    private KafkaHelper kafkaHelper;
//
//    private WireMockServer wireMockServer;
//
//    @Bean
//    public KafkaHelper kafkaHelper(@Value("@{kafka.server}") String kafkaServer) {
//        return new KafkaHelper(kafkaServer, "cucumber").startConsumingAvroTopics(LetterMessage.TOPIC);
//    }
//
//    @Given("setup")
//    public void beforeScenario() {
//        wireMockServer = new WireMockServer(wireMockConfig().port(10864));
//        wireMockServer.start();
//    }
//
//    @After
//    public void afterScenario(){
//        if (Objects.nonNull(wireMockServer)) {
//            wireMockServer.stop();
//        }
//        kafkaHelper.clearAllConsumedMessages();
//    }
//
//    @Given("Search One does not have any post code info")
//    public void searchOneDoesNotHaveAnyPostCodeInfo() {
//        wireMockServer.stubFor(get(urlMatching("/api/v1/postcode.*"))
//                .willReturn(aResponse()
//                        .withStatus(404)
//                ));
//    }
//
//    @Then("{int} messages were sent to onwards to search one")
//    public void messagesWereSentToOnwardsToSearchOne(int numberOfMessages) throws InterruptedException {
//        List<PostcodeAddress> postcodeAddressList = kafkaHelper.getAndClearConsumedMessages("test-topic", PostcodeAddress.class, numberOfMessages, 10000);
//
//        assertThat(postcodeAddressList).hasSize(numberOfMessages);
//    }
}
