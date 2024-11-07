package it.itsincom.webdev2024.service;

import it.itsincom.webdev2024.persistence.model.Prodotto;
import it.itsincom.webdev2024.persistence.repository.ProdottoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProdottoService {

    private final ProdottoRepository prodottoRepository;

    public ProdottoService(ProdottoRepository prodottoRepository) {
        this.prodottoRepository = prodottoRepository;
    }

    public Prodotto getProdotto(String nome) {
        return prodottoRepository.getProdotto(nome);
    }

    public Prodotto addProdotto(Prodotto prodotto) {
        if (prodotto.getQuantita() < 1) {
            prodotto.setQuantita(1);
        }
        return prodottoRepository.addProdotto(prodotto);
    }

    public void deleteProdotto(int id) {
        prodottoRepository.deleteProdotto(id);
    }

    public void updateProdotto(Prodotto prodotto) {
        prodottoRepository.updateProdotto(prodotto);
    }

}
