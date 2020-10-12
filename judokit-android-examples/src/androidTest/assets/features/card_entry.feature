Feature: Enter card number

  @smoke
  Scenario: Valid card
    Given judo configuration object is set up
    And I am on card entry screen
    When I click card number field
    And I type in a valid card
    Then no validation errors should appear

  @smoke
  Scenario: invalid card
    Given I am on card entry screen
    When I click card number field
    And I type in an invalid card
    Then validation error should appear