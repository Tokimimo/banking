package com.nicomadry.Banking.itl.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.nicomadry.Banking.api.data.dto.IbanValidationDTO;
import com.nicomadry.Banking.itl.exception.DeserializationException;

import java.io.IOException;

/**
 * This deserializer is written for the {@link IbanValidationDTO}
 */
public class IbanValidationDeserializer extends JsonDeserializer<IbanValidationDTO> {
  @Override
  public IbanValidationDTO deserialize( JsonParser jsonParser,
                                        DeserializationContext deserializationContext ) throws IOException, JsonProcessingException
  {
    if ( jsonParser.currentToken() != JsonToken.START_OBJECT ) {
      throw new DeserializationException( "Received invalid JSON" );
    }

    final IbanValidationDTO ibanValidationDTO = new IbanValidationDTO();

    while ( jsonParser.nextToken() != JsonToken.END_OBJECT ) {
      final String tokenName = jsonParser.getCurrentName();
      jsonParser.nextToken();  //move to next token in JSON which will be the value of the previous token

      if ( !tokenName.equals( "iban" ) ) {
        switch ( tokenName ) {
          case "bic":
            final String bic = jsonParser.getValueAsString();
            ibanValidationDTO.setBic( bic );
            break;
          case "bank_code":
            final String bankCode = jsonParser.getValueAsString();
            ibanValidationDTO.setBankCode( bankCode );
            break;
          case "account_number":
            final String accountNumber = jsonParser.getValueAsString();
            ibanValidationDTO.setAccountNumber( accountNumber );
            break;
          case "country_code":
            final String countryCode = jsonParser.getValueAsString();
            ibanValidationDTO.setCountryCode( countryCode );
          case "valid":
            final Boolean valid = jsonParser.getValueAsBoolean();
            ibanValidationDTO.setValid( valid );
          default:
            break;
        }
      }
    }

    jsonParser.close();

    return ibanValidationDTO;
  }
}
