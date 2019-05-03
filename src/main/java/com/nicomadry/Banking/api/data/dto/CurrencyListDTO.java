package com.nicomadry.Banking.api.data.dto;

import java.io.Serializable;
import java.util.Map;

public class CurrencyListDTO implements Serializable {

  private Map<String, CurrencyDTO> results;

  public CurrencyListDTO()
  {
  }

  public Map<String, CurrencyDTO> getResults()
  {
    return results;
  }

  public void setResults( Map<String, CurrencyDTO> results )
  {
    this.results = results;
  }
}
