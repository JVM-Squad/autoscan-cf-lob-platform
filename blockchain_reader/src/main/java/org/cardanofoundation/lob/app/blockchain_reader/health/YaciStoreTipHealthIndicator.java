package org.cardanofoundation.lob.app.blockchain_reader.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardanofoundation.lob.app.blockchain_reader.service.ChainSyncService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component("yaci_store_chain_sync")
@Slf4j
@RequiredArgsConstructor
@ConditionalOnBean(ChainSyncService.class)
public class YaciStoreTipHealthIndicator implements HealthIndicator {

    private final ChainSyncService chainSyncService;

    @Override
    public Health health() {
        var syncStatus = chainSyncService.getSyncStatus(false);

        if (syncStatus.isSynced()) {
            return Health
                    .up()
                    .withDetail("message", "Yaci-Store synced with the original chain!")
                    .withDetail("diffSlots", syncStatus.diff().orElse(-1L))
                    .build();
        }

        if (syncStatus.ex().isPresent()) {
            return Health
                    .down()
                    .withDetail("message", "Yaci-Store error...")
                    .withDetail("diffSlots", syncStatus.diff().orElse(-1L))
                    .withException(syncStatus.ex().get())
                    .build();
        }

        return Health
                .down()
                .withDetail("message", "Yaci-Store is syncing...")
                .withDetail("diffSlots", syncStatus.diff().orElse(-1L))
                .build();
    }

}
