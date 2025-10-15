package iscteiul.ista.exportpdf;

import iscteiul.ista.exportpdf.ExportPDF;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface ExportPDFRepository extends JpaRepository<ExportPDF, Long>, JpaSpecificationExecutor<ExportPDF> {

    // If you don't need a total row count, Slice is better than Page as it only performs a select query.
    // Page performs both a select and a count query.
    Slice<ExportPDF> findAllBy(Pageable pageable);
}
