package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.repository.UFRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UfService {
    private final UFRepository ufRepository;


}
