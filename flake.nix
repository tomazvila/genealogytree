{
  description = "Geneinator - Genealogy Tree Application";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = nixpkgs.legacyPackages.${system};
      in
      {
        devShells.default = pkgs.mkShell {
          buildInputs = with pkgs; [
            # Java Development
            jdk21
            gradle

            # Node.js for React frontend
            nodejs_22
            nodePackages.npm

            # Database
            postgresql_16

            # Message Queue (for local development/testing)
            rabbitmq-server

            # Useful tools
            git
            docker
            docker-compose

            # Image processing (for the image worker)
            imagemagick
            vips
          ];

          shellHook = ''
            echo "==========================================="
            echo "  Geneinator Development Environment"
            echo "==========================================="
            echo ""
            echo "Java:       $(java --version 2>&1 | head -1)"
            echo "Gradle:     $(gradle --version 2>&1 | grep 'Gradle' | head -1)"
            echo "Node.js:    $(node --version)"
            echo "npm:        $(npm --version)"
            echo "PostgreSQL: $(psql --version)"
            echo ""
            echo "==========================================="

            # Set JAVA_HOME
            export JAVA_HOME="${pkgs.jdk21}"

            # Generate JWT_SECRET if not already set (must be valid Base64, min 256 bits)
            if [ -z "$JWT_SECRET" ]; then
              export JWT_SECRET=$(${pkgs.openssl}/bin/openssl rand -base64 32)
              echo ""
              echo "JWT_SECRET auto-generated for this session."
            fi
          '';

          # Environment variables
          JAVA_HOME = "${pkgs.jdk21}";
        };
      }
    );
}
