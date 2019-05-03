package com.nicomadry.Banking.itl.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.nicomadry.Banking.api.data.dto.ExchangeRateDTO;
import com.nicomadry.Banking.itl.exception.DeserializationException;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * This deserializer is written for the {@link ExchangeRateDTO}
 */
public class ExchangeRateDeserializer extends JsonDeserializer<ExchangeRateDTO> {

  @Override
  public ExchangeRateDTO deserialize( JsonParser jsonParser,
                                      DeserializationContext deserializationContext ) throws IOException, JsonProcessingException
  {
    if ( jsonParser.currentToken() != JsonToken.START_OBJECT ) {
      throw new DeserializationException( "Received invalid JSON" );
    }

    final ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO();

    while ( jsonParser.nextToken() != JsonToken.END_OBJECT ) {
      final String exchange = jsonParser.getCurrentName();
      final String[] currencies = exchange.split( "_" );

      exchangeRateDTO.setExchange( exchange );

      // Since I know that the API returns the currency it is getting exchanged from first in api version v7,
      // it is safe to access the array static. This needs to be checked when changing to a different api version.
      exchangeRateDTO.setCurrencyFrom( currencies[0] );
      exchangeRateDTO.setCurrencyTo( currencies[1] );

      jsonParser.nextToken();  //move to next token in JSON

      final BigDecimal exchangeRate = jsonParser.getDecimalValue();

      exchangeRateDTO.setRate( exchangeRate );
    }
    jsonParser.close();

    return exchangeRateDTO;
  }


}
