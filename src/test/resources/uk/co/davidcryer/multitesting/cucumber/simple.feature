Feature: Simple

  Scenario: Create new simple
    When a new simple is posted
    Then a new simple is created matching request
    And the simple post response matches the request with generated id

  Scenario: Get existing simple
    Given a simple exists
    When the simple is fetched
    Then the response matches the simple