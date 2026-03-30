package com.estoque.sistema.service;

import com.estoque.sistema.dto.ItemFichaTecnicaRequestDTO;
import com.estoque.sistema.dto.ItemFichaTecnicaResponseDTO;
import com.estoque.sistema.dto.ProdutoRequestDTO;
import com.estoque.sistema.dto.ProdutoResponseDTO;
import com.estoque.sistema.exception.ResourceNotFoundException;
import com.estoque.sistema.model.Insumo;
import com.estoque.sistema.model.ItemFichaTecnica;
import com.estoque.sistema.model.Produto;
import com.estoque.sistema.repository.InsumoRepository;
import com.estoque.sistema.repository.ProdutoRepository;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@org.springframework.validation.annotation.Validated
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final InsumoRepository insumoRepository;
    private final ImageStorageService imageStorageService;

    public ProdutoService(ProdutoRepository produtoRepository,
            InsumoRepository insumoRepository,
            ImageStorageService imageStorageService) {
        this.produtoRepository = produtoRepository;
        this.insumoRepository = insumoRepository;
        this.imageStorageService = imageStorageService;
    }

    @Transactional
    @CacheEvict(value = "cardapio", allEntries = true)
    @TimeLimiter(name = "default")
    @Retry(name = "default")
    public ProdutoResponseDTO criarProduto(@NonNull @Valid ProdutoRequestDTO dto, MultipartFile imagem) {
        Produto produto = mapToEntity(dto);

        if (imagem != null && !imagem.isEmpty()) {
            produto.setUrlImagem(buildImageUrl(imageStorageService.storeFile(imagem)));
        }

        processarFichaTecnica(produto, dto.getItensFicha());

        Produto salvo = java.util.Objects.requireNonNull(produtoRepository.save(produto));
        return mapToResponseDTO(salvo);
    }

    public Page<ProdutoResponseDTO> listarTodos(@NonNull Pageable pageable) {
        return produtoRepository.findAll(pageable).map(this::mapToResponseDTO);
    }

    @Cacheable(value = "cardapio")
    public List<ProdutoResponseDTO> listarCardapioPublico() {
        return produtoRepository.findAllByDisponivelTrue().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<ProdutoResponseDTO> buscarPorId(@NonNull Long id) {
        return produtoRepository.findById(id).map(this::mapToResponseDTO);
    }

    @Transactional
    @CacheEvict(value = "cardapio", allEntries = true)
    @TimeLimiter(name = "default")
    @Retry(name = "default")
    public ProdutoResponseDTO atualizarProduto(@NonNull Long id, @NonNull @Valid ProdutoRequestDTO dto,
            MultipartFile novaImagem) {
        return produtoRepository.findById(id).map(produto -> {
            produto.setNome(dto.getNome());
            produto.setDescricao(dto.getDescricao());
            produto.setPreco(dto.getPreco());
            produto.setQuantidade(dto.getQuantidade());
            produto.setCategoria(dto.getCategoria());

            if (novaImagem != null && !novaImagem.isEmpty()) {
                produto.setUrlImagem(buildImageUrl(imageStorageService.storeFile(novaImagem)));
            }

            processarFichaTecnica(produto, dto.getItensFicha());

            return mapToResponseDTO(produtoRepository.save(produto));
        }).orElseThrow(() -> new ResourceNotFoundException("Produto não localizado."));
    }

    @Transactional
    @CacheEvict(value = "cardapio", allEntries = true)
    public ProdutoResponseDTO alterarDisponibilidade(@NonNull Long id, boolean disponivel) {
        return produtoRepository.findById(id).map(produto -> {
            produto.setDisponivel(disponivel);
            return mapToResponseDTO(produtoRepository.save(produto));
        }).orElseThrow(() -> new ResourceNotFoundException("Produto não localizado."));
    }

    @Transactional
    @CacheEvict(value = "cardapio", allEntries = true)
    public void deletarProduto(@NonNull Long id) {

        produtoRepository.deleteById(id);
    }

    private void processarFichaTecnica(Produto produto, List<ItemFichaTecnicaRequestDTO> itensDto) {
        produto.getItensFicha().clear();
        if (itensDto != null) {
            for (ItemFichaTecnicaRequestDTO itemDto : itensDto) {
                Insumo insumo = insumoRepository.findById(itemDto.getInsumoId())
                        .orElseThrow(() -> new ResourceNotFoundException("Insumo não encontrado: " + itemDto.getInsumoId()));

                ItemFichaTecnica item = new ItemFichaTecnica();
                item.setProduto(produto);
                item.setInsumo(insumo);
                item.setQuantidade(itemDto.getQuantidade());
                produto.getItensFicha().add(item);
            }
        }
    }

    private String buildImageUrl(String nomeArquivo) {
        if (nomeArquivo == null)
            return null;
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/imagens/")
                .path(nomeArquivo)
                .toUriString();
    }

    private Produto mapToEntity(ProdutoRequestDTO dto) {
        Produto produto = new Produto();
        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setQuantidade(dto.getQuantidade());
        produto.setCategoria(dto.getCategoria());
        produto.setDisponivel(true);
        return produto;
    }

    private ProdutoResponseDTO mapToResponseDTO(Produto produto) {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setDescricao(produto.getDescricao());
        dto.setPreco(produto.getPreco());
        dto.setQuantidade(produto.getQuantidade());
        dto.setUrlImagem(produto.getUrlImagem());
        dto.setDisponivel(produto.getDisponivel());
        dto.setCategoria(produto.getCategoria());

        if (produto.getItensFicha() != null) {
            dto.setItensFicha(produto.getItensFicha().stream().map(item -> {
                ItemFichaTecnicaResponseDTO itemDto = new ItemFichaTecnicaResponseDTO();
                itemDto.setId(item.getId());
                itemDto.setInsumoId(item.getInsumo().getId());
                itemDto.setInsumoNome(item.getInsumo().getNome());
                itemDto.setQuantidade(item.getQuantidade());
                return itemDto;
            }).collect(Collectors.toList()));
        }

        return dto;
    }
}
