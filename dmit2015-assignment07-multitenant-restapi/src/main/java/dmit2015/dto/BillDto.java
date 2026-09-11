package dmit2015.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.json.bind.annotation.JsonbDateFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillDto {

    private Long id;

    private String payeeName;

    private String username;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonbDateFormat("MMM dd, yyyy")
    private LocalDate dueDate = LocalDate.now().plusWeeks(2);

    private BigDecimal paymentDue;

    private boolean paid;

    private Integer version;

}
