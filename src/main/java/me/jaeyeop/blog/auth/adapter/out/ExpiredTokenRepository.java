package me.jaeyeop.blog.auth.adapter.out;

import org.springframework.data.repository.CrudRepository;

public interface ExpiredTokenRepository extends CrudRepository<ExpiredToken, String> {

}
