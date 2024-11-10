package it.itsincom.webdev2024.service;

import it.itsincom.webdev2024.persistence.model.Prodotto;
import it.itsincom.webdev2024.persistence.repository.ProdottoRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ProdottoService {

    private final ProdottoRepository prodottoRepository;

    public ProdottoService(ProdottoRepository prodottoRepository) {
        this.prodottoRepository = prodottoRepository;
    }

    public List<Prodotto> getAllProdotti(){
        return prodottoRepository.getAllProdotti();
    }

    public Prodotto getProdotto(String nome) {
        return prodottoRepository.getProdotto(nome);
    }

    public Prodotto addProdotto(Prodotto prodotto) {
        return prodottoRepository.addProdotto(prodotto);
    }

    public void deleteProdotto(int id) {
        prodottoRepository.deleteProdotto(id);
    }

    public Prodotto updateProdotto(Prodotto prodotto) {
        return prodottoRepository.updateProdotto(prodotto);
    }

}
