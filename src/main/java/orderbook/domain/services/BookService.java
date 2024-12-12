package orderbook.domain.services;

import orderbook.dataprovider.repositories.BookRepository;
import orderbook.domain.models.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;

    }

    public List<Book> findAllBooks(){
            return bookRepository.findAll();
    }

    public Book findBookById(Long id){
        return bookRepository.findById(id).orElseThrow();
    }



}
