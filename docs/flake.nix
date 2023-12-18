# this file is used only for local dev dependencies can can be just ignore by everyone else
{
  description = "nosql_arbeit";

  # default packages
  inputs.nixpkgs.url = "github:NixOS/nixpkgs/release-23.11";
  inputs.devpkgs.url = "gitlab:homelab/devpkgs?host=gitlab.potatolab.xyz";

  outputs = { devpkgs, ... }@inputs:
    let
      lib = devpkgs.mkLib inputs;
    in
    {

      devShells = lib.mkDevShells ({ pkgs, ... }: {
        packages = with pkgs; [
          my.suites.writing
        ];
      });
    };
}
