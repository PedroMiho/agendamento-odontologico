package com.aula.agendamento_odontologico.controller;

import com.aula.agendamento_odontologico.dto.DadosAtualizacaoDentista;
import com.aula.agendamento_odontologico.dto.DadosCadastroDentista;
import com.aula.agendamento_odontologico.dto.DadosDetalhamentoDentista;
import com.aula.agendamento_odontologico.dto.DadosListagemDentista;
import com.aula.agendamento_odontologico.model.Dentista;
import com.aula.agendamento_odontologico.repository.DentistaRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/dentista")
@RestController
public class ControllerDentista {

    @Autowired //Deixa a reponsabilidade para o spring instanciar
    private DentistaRepository repository;

    @PostMapping
    @Transactional
    public void cadastroDentista(@RequestBody @Valid DadosCadastroDentista dadosDentista) {

        repository.save(new Dentista(dadosDentista));
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemDentista>> listarDentistas(Pageable pageable) {
        var page = repository.findAllByAtivoTrue(pageable).map(DadosListagemDentista::new);
        return ResponseEntity.ok(page);
    }

    @PutMapping
    @Transactional
    public ResponseEntity alterarDadosDentista(@RequestBody @Valid DadosAtualizacaoDentista dadosDentista) {
        var dentista = repository.getReferenceById(dadosDentista.id());
        dentista.atualizarDadosDentista(dadosDentista);
        return ResponseEntity.ok(new DadosDetalhamentoDentista(dentista));
    }

    @DeleteMapping("{id}")
    @Transactional
    public ResponseEntity excluirDentista(@PathVariable Long id) {
        var dentista = repository.getReferenceById(id);
        dentista.atualizarStatus();

        return ResponseEntity.noContent().build();
    }

}
