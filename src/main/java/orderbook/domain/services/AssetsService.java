package orderbook.domain.services;

import orderbook.dataprovider.exceptions.BusinessException;
import orderbook.dataprovider.repositories.AssetRepository;
import orderbook.domain.models.Asset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static orderbook.domain.messages.Messages.ATIVO_INVALIDO;

@Service
public class AssetsService {

    private final AssetRepository assetRepository;

    @Autowired
    public AssetsService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;

    }

    public List<Asset> findAllAssets(){
            return assetRepository.findAll();
    }

    public Asset findAssetsById(Long id){
        return assetRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ATIVO_INVALIDO));
    }



}
