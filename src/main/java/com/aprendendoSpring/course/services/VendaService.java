package com.aprendendoSpring.course.services;

import com.aprendendoSpring.course.dtos.ItemVendaRequestDTO;
import com.aprendendoSpring.course.dtos.VendaRequestDTO;
import com.aprendendoSpring.course.entities.Estoque;
import com.aprendendoSpring.course.entities.ItemVenda;
import com.aprendendoSpring.course.entities.Produto;
import com.aprendendoSpring.course.entities.Venda;
import com.aprendendoSpring.course.entities.enums.StatusVenda;
import com.aprendendoSpring.course.exceptions.BusinessException;
import com.aprendendoSpring.course.exceptions.ResourceNotFoundException;
import com.aprendendoSpring.course.repositories.ClienteRepository;
import com.aprendendoSpring.course.repositories.EstoqueRepository;
import com.aprendendoSpring.course.repositories.ProdutoRepository;
import com.aprendendoSpring.course.repositories.VendaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;

    public VendaService(VendaRepository vendaRepository, ClienteRepository clienteRepository,
                        ProdutoRepository produtoRepository, EstoqueRepository estoqueRepository) {
        this.vendaRepository = vendaRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueRepository = estoqueRepository;
    }

    public List<Venda> findAll() {
        return vendaRepository.findAll();
    }

    public Venda findById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("Informe um ID de venda válido.");
        }
        return vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada. ID: " + id));
    }

    public Venda insert(VendaRequestDTO dto) {
        validarVenda(dto);

        Venda venda = new Venda();

        if (dto.clienteId() != null && dto.clienteId() > 0) {
            venda.setCliente(clienteRepository.findById(dto.clienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado. ID: " + dto.clienteId())));
        }

        for (ItemVendaRequestDTO itemDto : dto.itens()) {
            Produto produto = produtoRepository.findById(itemDto.produtoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado. ID: " + itemDto.produtoId()));

            Estoque estoque = estoqueRepository.findByProduto_IdProduto(itemDto.produtoId())
                    .orElseThrow(() -> new BusinessException(
                            "Estoque não cadastrado para o produto: " + produto.getNome()));

            if (estoque.getQuantidadeAtual() < itemDto.quantidade()) {
                throw new BusinessException("Estoque insuficiente para \"" + produto.getNome()
                        + "\". Disponível: " + estoque.getQuantidadeAtual()
                        + ", solicitado: " + itemDto.quantidade());
            }

            estoque.setQuantidadeAtual(estoque.getQuantidadeAtual() - itemDto.quantidade());
            estoqueRepository.save(estoque);

            venda.getItens().add(new ItemVenda(produto, venda, itemDto.quantidade()));
        }

        venda.calcularTotal();
        return vendaRepository.save(venda);
    }

    public Venda finalizarVenda(Long id) {
        Venda venda = findById(id);
        if (venda.getStatusVenda() == StatusVenda.CANCELADA) {
            throw new BusinessException("Não é possível finalizar uma venda cancelada.");
        }
        venda.finalizarVenda();
        return vendaRepository.save(venda);
    }

    public Venda cancelarVenda(Long id) {
        Venda venda = findById(id);
        if (venda.getStatusVenda() == StatusVenda.FINALIZADA) {
            throw new BusinessException("Não é possível cancelar uma venda finalizada.");
        }
        venda.cancelarVenda();
        return vendaRepository.save(venda);
    }

    public List<Venda> buscarVendasPorStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new BusinessException("Informe um status de venda válido.");
        }
        return vendaRepository.buscarVendasPorStatus(status.toUpperCase());
    }

    private void validarVenda(VendaRequestDTO dto) {
        if (dto == null) {
            throw new BusinessException("Informe os dados da venda.");
        }
        if (dto.itens() == null || dto.itens().isEmpty()) {
            throw new BusinessException("A venda deve ter pelo menos um item.");
        }
        for (ItemVendaRequestDTO item : dto.itens()) {
            if (item.produtoId() == null || item.produtoId() <= 0) {
                throw new BusinessException("Todos os itens da venda devem informar um produto válido.");
            }
            if (item.quantidade() == null || item.quantidade() <= 0) {
                throw new BusinessException("A quantidade de cada item deve ser maior que zero.");
            }
        }
    }
}
