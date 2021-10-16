package dev.marcosalmeida.beerinventory.services;

import dev.marcosalmeida.beerinventory.config.JmsConfig;
import dev.marcosalmeida.beerinventory.domain.BeerInventory;
import dev.marcosalmeida.beerinventory.repository.BeerInventoryRepository;
import dev.marcosalmeida.brewery.model.events.BeerDto;
import dev.marcosalmeida.brewery.model.events.NewInventoryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class NewInventoryListener {

    private final BeerInventoryRepository beerInventoryRepository;

    @Transactional
    @JmsListener(destination = JmsConfig.NEW_INVENTORY_QUEUE)
    public void listen(NewInventoryEvent event) {
        log.debug("Got inventory: {}", event.toString());

        BeerDto beerDto = event.getBeerDto();

        BeerInventory beerInventory = BeerInventory.builder()
                .beerId(event.getBeerDto().getId())
                .upc(beerDto.getUpc())
                .quantityOnHand(beerDto.getQuantityOnHand())
                .build();

        beerInventoryRepository.saveAndFlush(beerInventory);
    }
}
