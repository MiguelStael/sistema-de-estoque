package com.estoque.sistema.service;

import com.estoque.sistema.dto.CategoriaResponseDTO;
import com.estoque.sistema.dto.ComposicaoRequestDTO;
import com.estoque.sistema.dto.ComposicaoResponseDTO;
import com.estoque.sistema.dto.ProdutoRequestDTO;
import com.estoque.sistema.dto.ProdutoResponseDTO;
import com.estoque.sistema.exception.ResourceNotFoundException;
import com.estoque.sistema.model.Categoria;
import com.estoque.sistema.model.Ingrediente;
import com.estoque.sistema.model.Composicao;
import com.estoque.sistema.model.Produto;
import com.estoque.sistema.repository.CategoriaRepository;
import com.estoque.sistema.repository.IngredienteRepository;
import com.estoque.sistema.repository.ProdutoRepository;
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
    private final IngredienteRepository ingredienteRepository;
    private final CategoriaRepository categoriaRepository;
    private final ImageStorageService imageStorageService;

    public ProdutoService(ProdutoRepository produtoRepository,
            IngredienteRepository ingredienteRepository,
            CategoriaRepository categoriaRepository,
            ImageStorageService imageStorageService) {
        this.produtoRepository = produtoRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.categoriaRepository = categoriaRepository;
        this.imageStorageService = imageStorageService;
    }

    @Transactional
    @CacheEvict(value = "cardapio", allEntries = true)
    public ProdutoResponseDTO criarProduto(@NonNull @Valid ProdutoRequestDTO dto, MultipartFile imagem) {
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
            .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada ID: " + dto.getCategoriaId()));
            
        Produto produto = mapToEntity(dto);
        produto.setCategoria(categoria);

        if (imagem != null && !imagem.isEmpty()) {
            produto.setUrlImagem(buildImageUrl(imageStorageService.storeFile(imagem)));
        }

        processarComposicao(produto, dto.getItensComposicao());

        Produto salvoTemp = produtoRepository.save(produto);
        if (salvoTemp == null) {
            throw new RuntimeException("Erro ao salvar produto.");
        }
        return mapToResponseDTO(salvoTemp);
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
    public ProdutoResponseDTO atualizarProduto(@NonNull Long id, @NonNull @Valid ProdutoRequestDTO dto,
            MultipartFile novaImagem) {
        return produtoRepository.findById(id).map(produto -> {
            produto.setNome(dto.getNome());
            produto.setDescricao(dto.getDescricao());
            produto.setPreco(dto.getPreco());
            produto.setQuantidade(dto.getQuantidade());
            
            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada ID: " + dto.getCategoriaId()));
            produto.setCategoria(categoria);

            if (novaImagem != null && !novaImagem.isEmpty()) {
                produto.setUrlImagem(buildImageUrl(imageStorageService.storeFile(novaImagem)));
            }

            processarComposicao(produto, dto.getItensComposicao());

            return mapToResponseDTO(produtoRepository.save(produto));
        }).orElseThrow(() -> new ResourceNotFoundException("Produto nao localizado."));
    }

    @Transactional
    @CacheEvict(value = "cardapio", allEntries = true)
    public ProdutoResponseDTO alterarDisponibilidade(@NonNull Long id, boolean disponivel) {
        return produtoRepository.findById(id).map(produto -> {
            produto.setDisponivel(disponivel);
            return mapToResponseDTO(produtoRepository.save(produto));
        }).orElseThrow(() -> new ResourceNotFoundException("Produto nao localizado."));
    }

    @Transactional
    @CacheEvict(value = "cardapio", allEntries = true)
    public void deletarProduto(@NonNull Long id) {
        produtoRepository.deleteById(id);
    }

    private void processarComposicao(Produto produto, List<ComposicaoRequestDTO> itensDto) {
        produto.getItensComposicao().clear();
        if (itensDto != null) {
            for (ComposicaoRequestDTO itemDto : itensDto) {
                Long ingredienteId = itemDto.getIngredienteId();
                if (ingredienteId == null) {
                    throw new IllegalArgumentException("ID do ingrediente não pode ser nulo na composição.");
                }

                Ingrediente ingrediente = ingredienteRepository.findById(ingredienteId)
                        .orElseThrow(() -> new ResourceNotFoundException("Ingrediente nao encontrado: " + ingredienteId));

                Composicao item = new Composicao();
                item.setProduto(produto);
                item.setIngrediente(ingrediente);
                item.setQuantidade(itemDto.getQuantidade());
                produto.getItensComposicao().add(item);
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
        
        if (produto.getCategoria() != null) {
            CategoriaResponseDTO catDto = new CategoriaResponseDTO();
            catDto.setId(produto.getCategoria().getId());
            catDto.setNome(produto.getCategoria().getNome());
            catDto.setDescricao(produto.getCategoria().getDescricao());
            catDto.setAtiva(produto.getCategoria().getAtiva());
            dto.setCategoria(catDto);
        }

        if (produto.getItensComposicao() != null) {
            dto.setItensComposicao(produto.getItensComposicao().stream().map(item -> {
                ComposicaoResponseDTO itemDto = new ComposicaoResponseDTO();
                itemDto.setId(item.getId());
                itemDto.setIngredienteId(item.getIngrediente().getId());
                itemDto.setIngredienteNome(item.getIngrediente().getNome());
                itemDto.setQuantidade(item.getQuantidade());
                return itemDto;
            }).collect(Collectors.toList()));
        }

        return dto;
    }
}
