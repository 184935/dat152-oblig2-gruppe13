/**
 * 
 */
package no.hvl.dat152.rest.ws.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.AuthorNotFoundException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.repository.AuthorRepository;

/**
 * @author tdoy
 */
@Service
public class AuthorService {

	// TODO copy your solutions from previous tasks!

	@Autowired
	private AuthorRepository authorRepository;


	public Author findById(int id) throws AuthorNotFoundException {

		Author author = authorRepository.findById(id)
				.orElseThrow(()-> new AuthorNotFoundException("Author with the id: "+id+ "not found!"));

		return author;
	}

	// TODO public saveAuthor(Author author)
	public Author saveAuthor(Author author) {

		return authorRepository.save(author);
	}


	// TODO public Author updateAuthor(Author author, int id)

	public Author updateAuthor(Author author, int id) throws AuthorNotFoundException {

		if (!authorRepository.existsById(id)) {
			throw new AuthorNotFoundException("Author with the id: "+id+" not found!");
		}
		author.setAuthorId(id);
		return authorRepository.save(author);
	}

	// TODO public List<Author> findAll()
	public List<Author> findAll() {

		return (List<Author>) authorRepository.findAll();
	}

	// TODO public void deleteById(int id) throws AuthorNotFoundException

	public void deleteById(int id) throws AuthorNotFoundException {

		if (!authorRepository.existsById(id)) {
			throw new AuthorNotFoundException("Author with the id: " + id + " not found!");
		}
		authorRepository.deleteById(id);
	}
	// TODO public Set<Book> findBooksByAuthorId(int id)

	public Set<Book> findBooksByAuthorId(int id) throws AuthorNotFoundException {

		if (!authorRepository.existsById(id)) {
			return null;
		}
		Author author = findById(id);
		return author.getBooks();
	}

	public Author findById(long id) {

		// TODO

		return null;
	}
}
