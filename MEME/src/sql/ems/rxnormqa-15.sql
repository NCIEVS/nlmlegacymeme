# 15. SCD/SBD Concepts with 'Solid' dose forms expressed as "ML".

SELECT DISTINCT a.concept_id
FROM classes a, atoms b
WHERE a.source in (SELECT current_name FROM source_version WHERE source='RXNORM')
  AND a.tty in ('SCD','SBD')
  AND a.tobereleased in ('Y','y')
  AND a.atom_id = b.atom_id
  AND atom_name like '%ML % Solid%';
