package com.code.truck.finances.app.infrastructure;

import com.code.truck.finances.app.core.domain.repository.TransactionRepository;
import com.code.truck.finances.app.core.domain.repository.UserRepository;
import com.code.truck.finances.app.core.domain.usecase.CreateTransactionUseCase;
import com.code.truck.finances.app.core.domain.usecase.GetTransactionsByUserUseCase;
import com.code.truck.finances.app.core.domain.usecase.GetUserByIdUseCase;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class FinancesAppApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(FinancesAppApplication.class, args);
	}

	// Use case beans configuration
	@Bean
	public CreateTransactionUseCase createTransactionUseCase(TransactionRepository transactionRepository) {
		return new CreateTransactionUseCase(transactionRepository);
	}

	@Bean
	public GetTransactionsByUserUseCase getTransactionsByUserUseCase(TransactionRepository transactionRepository) {
		return new GetTransactionsByUserUseCase(transactionRepository);
	}

	@Bean
	public GetUserByIdUseCase getUserByIdUseCase(UserRepository userRepository) {
		return new GetUserByIdUseCase(userRepository);
	}

	/**
	 * Cleanup method to handle JNA threads gracefully
	 */
	@PreDestroy
	public void preDestroy() {
		// Allow time for JNA Cleaner thread to clean up
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		// Find and stop all non-daemon threads that might cause leaks
		Thread[] threads = new Thread[Thread.activeCount()];
		Thread.enumerate(threads);
		for (Thread thread : threads) {
			if (thread != null &&
					!thread.isDaemon() &&
					thread.isAlive() &&
					(thread.getName().startsWith("JNA") ||
							thread.getName().startsWith("vaadin-dev-server"))) {
				try {
					thread.interrupt();
				} catch (Exception e) {
					// Log but continue shutdown process
					System.err.println("Error stopping thread: " + thread.getName());
				}
			}
		}
	}
}