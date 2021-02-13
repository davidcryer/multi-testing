Feature: Large

  Scenario: Create new large
    When a new large is posted
    Then a new large is created matching request
    And the large post response matches the request with generated id

  Scenario: Get existing large
    Given a large exists
    When the large is fetched
    Then the response matches the large