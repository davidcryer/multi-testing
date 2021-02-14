package uk.co.davidcryer.multitesting.letter;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.davidcryer.multitesting.generated.tables.pojos.Address;
import uk.co.davidcryer.multitesting.generated.tables.pojos.Letter;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LetterService {
    private final LetterRepository letterRepository;
    private final AddressRepository addressRepository;

    void add(LetterMessage letterMessage) {
        var unsavedLetter = toLetter(letterMessage);
        var unsavedAddress = toAddress(letterMessage.getRecipientAddress());
        unsavedAddress.setId(UUID.randomUUID().toString());
        var savedAddress = addressRepository.add(unsavedAddress);
        unsavedLetter.setRecipientAddress(savedAddress.getId());
        letterRepository.add(unsavedLetter);
    }

    private Letter toLetter(LetterMessage letterMessage) {
        return new Letter(
                letterMessage.getId(),
                letterMessage.getSender(),
                letterMessage.getRecipient(),
                null,
                letterMessage.getMessage()
        );
    }

    private Address toAddress(LetterMessage.Address addressMessage) {
        return new Address(
                null,
                addressMessage.getBuildingNumber(),
                addressMessage.getOrganisation(),
                addressMessage.getAddressLine1(),
                addressMessage.getAddressLine2(),
                addressMessage.getCounty(),
                addressMessage.getTown(),
                addressMessage.getPostcode()
        );
    }

    Optional<LetterMessage> get(String id) {
        return letterRepository.get(id).map(letter -> {
            //cannot be empty due to foreign key constraint
            //noinspection OptionalGetWithoutIsPresent
            var address = addressRepository.get(letter.getRecipientAddress()).get();
            return toRequest(letter, address);
        });
    }

    private LetterMessage toRequest(Letter letter, Address address) {
        return new LetterMessage(
                letter.getId(),
                letter.getSender(),
                letter.getRecipient(),
                new LetterMessage.Address(
                        address.getBuildingNumber(),
                        address.getOrganisation(),
                        address.getAddressLine_1(),
                        address.getAddressLine_2(),
                        address.getCounty(),
                        address.getTown(),
                        address.getPostcode()
                ),
                letter.getMessage()
        );
    }
}
